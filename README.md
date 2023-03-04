# Update Rekordbox keys/tonalities from added Mixed In Key comments.

Assumes that if Mixed in Key has been used the Comments will start with the key/tonality it identified, e.g. "1A - Energy 6".

For all tracks in the rekordbox.xml the Rekordbox tonality is compared with the tonality in the Comments and if it differs the one from the Comments is used.

## Instructions

1. In Main.java:
    1. Update the location of your recordbox.xml
    1. Update the scale format to be output (Camelot or Classical).
1. Launch Main.java
1. If tonalities have been updated then a new version of rekordbox.xml named "puddi_uk_updated_rekordbox.xml" will be written beside your existing one.

_Note: You can force the updated file to be written by changing the value of FORCE_UPDATE in TonalityUpdater.java. This allows conversion of scale formats even if the Rekordbox and Mixed in Key tonalities are the same._
