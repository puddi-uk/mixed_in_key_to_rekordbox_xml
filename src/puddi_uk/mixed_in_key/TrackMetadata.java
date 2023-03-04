package puddi_uk.mixed_in_key;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import puddi_uk.mixed_in_key.TonalityHelper.ScaleFormat;

public class TrackMetadata {

	// Matches the Comments field when it begins with (examples):
	// "11A - Energy 8"
	// "G♯m - Energy 4"
	// Single capture group at the start captures either "11A" or "G♯m" or "F" as tonalities.
	private static final String		MIK_COMMENTS_REGEX		= "((?:\\d{1,2}[AB])|(?:[A-G][♯m]{0,2})) - Energy \\d.*";
	private static final Pattern	MIK_COMMENTS_PATTERN	= Pattern.compile(MIK_COMMENTS_REGEX);

	private final String			name;
	private final String			artist;
	private final String			tonality;
	private final String			comments;

	private boolean					isTonalityInComments;
	private String					tonalityFromComments;

	public TrackMetadata(String name, String artist, String tonality, String comments, ScaleFormat desiredScaleFormat) {
		this.name = name;
		this.artist = artist;
		this.tonality = TonalityHelper.convertTonality(tonality, desiredScaleFormat);
		this.comments = comments;

		this.tonalityFromComments = TonalityHelper.convertTonality(extractTonalityFromCommments(), desiredScaleFormat);
	}

	public String getName() {
		return name;
	}

	public String getArtist() {
		return artist;
	}

	public String getTonality() {
		return tonality;
	}

	public String getComments() {
		return comments;
	}

	public String getTonalityFromComments() {
		return tonalityFromComments;
	}

	public boolean isTonalityInComments() {
		return isTonalityInComments;
	}

	public boolean isTonalityDifference() {
		return !tonality.equalsIgnoreCase(tonalityFromComments);
	}

	private String extractTonalityFromCommments() {
		Matcher commentsMatcher = MIK_COMMENTS_PATTERN.matcher(comments);

		// If the regex matched the "Comments" field group 1 will contain the Mixed in Key identified Tonality.
		if (commentsMatcher.matches()) {
			isTonalityInComments = true;
			return commentsMatcher.group(1);
		} else {
			isTonalityInComments = false;
			return tonality;
		}
	}

	@Override
	public String toString() {
		return "TrackMetadata [name=" + name + ", artist=" + artist + ", tonality=" + tonality + ", comments=" + comments + ", isTonalityInComments=" + isTonalityInComments + ", tonalityFromComments=" + tonalityFromComments + "]";
	}
	
	

}
