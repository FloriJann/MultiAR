@file:Suppress("ArrayInDataClass")

package inovex.ad.multiar.polyViewerModule.poly


data class Assets(
    val assets:List<Asset>
)

data class Asset(
    val name:String,                            // The unique identifier for the asset in the form: assets/{ASSET_ID}.
    val displayName:String,                     // The human-readable name, set by the asset's author.
    val authorName:String,                      // The author's publicly visible name.
    val description: String,                    // The human-readable description, set by the asset's author.
    val formats: List<Format>,                  // A list of Formats where each format describes one representation of the asset.
    val thumbnail: File                         // The thumbnail image for the asset.
)

data class File(
    val relativePath: String,                   // The path of the resource file relative to the root file.
                                                // For root or thumbnail files, this is just the filename.
    val url: String,                            // The URL where the file data can be retrieved.
    val contentType: String                     // The MIME content-type, such as image/png. For more information, see MIME types.
)

data class Format(
    val root: File,
    val resources: List<File>,                  // A list of dependencies of the root element.
    val formatComplexity: FormatComplexity,     // Complexity stats about this representation of the asset.
    val formatType: String                      // A short string that identifies the format type of this representation.
                                                // Possible values are: FBX, GLTF, GLTF2, OBJ, and TILT.
)

data class FormatComplexity(
    val triangleCount:String,                   // The estimated number of triangles.
    val lodHint: Int                            // A non-negative integer that represents the level of detail (LOD) of
                                                // this format relative to other formats of the same asset with the same
                                                // formatType. This hint allows you to sort formats from the
                                                // most-detailed (0) to least-detailed (integers greater than 0).
)

data class AssetThumbnail(
    val displayName: String,
    val authorName: String,
    val name: String,
    val thumbnailURL: String,
    val resourceURLs: Resources
)

data class Resources(
    val rootURL: String,
    val resources:List<String>
)




