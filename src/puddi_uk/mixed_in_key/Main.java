package puddi_uk.mixed_in_key;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import puddi_uk.mixed_in_key.TonalityHelper.ScaleFormat;

public class Main {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

		// UPDATE THIS TO POINT AT YOUR REKORDBOX.XML
		String rekordboxXmlFileLocation = "E:\\PIONEER\\rekordbox.xml";
		// UPDATE THIS TO CHANGE THE SCALE FORMAT.
		ScaleFormat desiredScaleFormat = ScaleFormat.CAMELOT;

		(new TonalityUpdater(rekordboxXmlFileLocation, desiredScaleFormat)).update();
	}

}