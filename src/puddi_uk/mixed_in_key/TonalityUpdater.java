package puddi_uk.mixed_in_key;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import puddi_uk.mixed_in_key.TonalityHelper.ScaleFormat;

public class TonalityUpdater {

	// If true will always write an updated file even if no changes were made.
	private static final boolean	FORCE_UPDATE			= true;
	
	private static final String		OUTPUT_FILE_NAME		= "puddi_uk_updated_rekordbox.xml";

	private static final String		XML_TAG_TRACK			= "TRACK";
	// Attribute which only appears in <TRACK> elements which are playlist references.
	private static final String		XML_ATTRIBUTE_KEY		= "Key";
	// Attributes which appear in <TRACK> elements which provide track metadata.
	private static final String		XML_ATTRIBUTE_TONALITY	= "Tonality";
	private static final String		XML_ATTRIBUTE_COMMENTS	= "Comments";
	private static final String		XML_ATTRIBUTE_NAME		= "Name";
	private static final String		XML_ATTRIBUTE_ARTIST	= "Artist";

	private File					rekordboxXmlFile;
	private ScaleFormat				desiredScaleFormat;

	public TonalityUpdater(String rekordboxXmlLocation, ScaleFormat scaleType) {
		rekordboxXmlFile = new File(rekordboxXmlLocation);
		this.desiredScaleFormat = scaleType;
	}

	public void update() {
		try {

			Document rekordboxXml = parseXml();
			boolean updatesMade = updateTonalityFromComment(rekordboxXml);
			if (updatesMade | FORCE_UPDATE) {
				System.out.println("\n|");
				System.out.println("| Updates were made; writing updated version: " + OUTPUT_FILE_NAME + ".");
				System.out.println("|");
				writeUpdatedRekordboxXml(rekordboxXml);
			} else {
				System.out.println("\n|======================================================================");
				System.out.println("| No updates were made so no updated file will be written.");
				System.out.println("|======================================================================");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private Document parseXml() throws ParserConfigurationException, SAXException, IOException {
		ensureRekordboxXmlExists();

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(rekordboxXmlFile);
		document.getDocumentElement().normalize();

		return document;
	}

	private void ensureRekordboxXmlExists() {
		if (!rekordboxXmlFile.exists()) {
			System.err.println("FATAL: No recordbox.xml found: " + rekordboxXmlFile.getAbsolutePath());
			System.exit(1);
		}
	}

	/**
	 * Iterate over the non-playlist TRACK elements in the XML. For each TRACK element that has a MIK comment with a key that differs from the Rekordbox tonality update it to use the MIK key.
	 * 
	 * @param rekordboxXml
	 */
	private boolean updateTonalityFromComment(Document rekordboxXml) {
		// Grab all the <TRACK> elements.
		NodeList tracks = rekordboxXml.getElementsByTagName(XML_TAG_TRACK);

		int trackCount = 0;
		int tracksUpdated = 0;

		// Iterate over each <TRACK> and update its "Tonality" attribute (AKA key) from the "Comments" attribute.
		for (int trackNodeIndex = 0; trackNodeIndex < tracks.getLength(); trackNodeIndex++) {

			Node track = tracks.item(trackNodeIndex);

			// <TRACK> elements can either contain metadata about the track or be references from playlists to <TRACK> elements, and in this latter case
			// the <TRACK> element has a single "Key" attribute. The lazy approach to ignoring <TRACK> elements in playlists is simply to ignore those with a "Key" attribute.
			if (isPlaylistEntry(track)) {
				continue;
			} else {
				trackCount++;
			}

			TrackMetadata trackMetadata = parseTrack(track);

			if (!trackMetadata.isTonalityInComments()) {
				System.out.println("WARNING: Skipped track! No MIK tonality found in Comments field: " + trackMetadata.getComments());
				continue;
			}

			if (trackMetadata.isTonalityDifference() || FORCE_UPDATE) {
				System.out.println("Updating tonality of: " + trackMetadata.getName() + " - " + trackMetadata.getArtist());
				System.out.println("  " + trackMetadata.getTonality() + " => " + trackMetadata.getTonalityFromComments() + "\n");

				// Update the value of the "Tonality" attribute, converting if necessary.
				Node tonalityAttribute = track.getAttributes().getNamedItem(XML_ATTRIBUTE_TONALITY);
				String updatedTonality = TonalityHelper.convertTonality(trackMetadata.getTonalityFromComments(), desiredScaleFormat);
				tonalityAttribute.setNodeValue(updatedTonality);

				tracksUpdated++;
			}

		}

		System.out.println("\n|======================================================================");
		System.out.println("| Finished examining tracks!");
		System.out.println("| Tracks Examined: " + trackCount);
		System.out.println("| Tracks Updated:  " + tracksUpdated);
		System.out.println("|======================================================================");

		return tracksUpdated > 0;
	}

	/**
	 * @param track
	 * @return true if the TRACK element is a reference from a playlist, otherwise false.
	 */
	private boolean isPlaylistEntry(Node track) {
		NamedNodeMap trackAttributes = track.getAttributes();
		return trackAttributes.getNamedItem(XML_ATTRIBUTE_KEY) != null;
	}

	/**
	 * @param track
	 * @return a TrackMetadata object describing the TRACK element.
	 */
	private TrackMetadata parseTrack(Node track) {
		NamedNodeMap trackAttributes = track.getAttributes();
		Node nameAttribute = trackAttributes.getNamedItem(XML_ATTRIBUTE_NAME);
		Node artistAttribute = trackAttributes.getNamedItem(XML_ATTRIBUTE_ARTIST);
		Node tonalityAttribute = trackAttributes.getNamedItem(XML_ATTRIBUTE_TONALITY);
		Node commentsAttribute = trackAttributes.getNamedItem(XML_ATTRIBUTE_COMMENTS);

		String name = nameAttribute.getNodeValue();
		String artist = artistAttribute.getNodeValue();
		String tonality = tonalityAttribute.getNodeValue();
		String comments = commentsAttribute.getNodeValue();

		return new TrackMetadata(name, artist, tonality, comments, desiredScaleFormat);
	}

	private void writeUpdatedRekordboxXml(Document rekordboxXml) throws TransformerFactoryConfigurationError, TransformerException {

		// Get the parent directory of the rekordbox.xml file where the output file will be written.
		Path rekordboxXmlParentDirPath = Paths.get(rekordboxXmlFile.getAbsolutePath()).getParent();
		Path outputRekordboxXmlPath = Paths.get(rekordboxXmlParentDirPath + File.separator + OUTPUT_FILE_NAME);

		System.out.println(outputRekordboxXmlPath.toString());

		DOMSource documentSource = new DOMSource(rekordboxXml);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		StreamResult result = new StreamResult(outputRekordboxXmlPath.toFile());
		transformer.transform(documentSource, result);
	}

}