import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeoResponse(
    @JsonProperty(value = "response")
    val response: Response
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Response(
    @JsonProperty(value = "GeoObjectCollection")
    val geoObjectCollection: GeoObjectCollection
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeoObjectCollection(
    @JsonProperty(value = "metaDataProperty")
    val metaDataProperty: MetadataProperty,
    @JsonProperty(value = "featureMember")
    val featureMember: List<FeatureMember>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MetadataProperty(
    @JsonProperty("GeocoderResponseMetaData")
    val geocoderResponseMetadata: GeocoderResponseMetadata
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeocoderResponseMetadata(
    val request: String,
    val results: String,
    val found: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FeatureMember(
    @JsonProperty(value = "GeoObject")
    val geoObject: GeoObject
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeoObject(
    @JsonProperty(value = "metaDataProperty")
    val metaDataProperty: GeoMetadataProperty?,
    @JsonProperty(value = "name")
    val name: String,
    @JsonProperty(value = "description")
    val description: String,
    @JsonProperty(value = "boundedBy")
    val boundedBy: BoundedBy,
    @JsonProperty(value = "uri")
    val uri: String,
    @JsonProperty(value = "Point")
    val point: Point
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeoMetadataProperty(
    @JsonProperty("GeocoderMetaData")
    val geocoderMetadata: GeocoderMetadata
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeocoderMetadata(
    val precision: String,
    val text: String,
    val kind: String,
    val Address: Address
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Address(
    val country_code: String,
    val formatted: String,
    val Components: List<Component>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Component(
    val kind: String,
    val name: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BoundedBy(
    @JsonProperty(value = "Envelope")
    val envelope: Envelope
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Envelope(
    @JsonProperty(value = "lowerCorner")
    val lowerCorner: String,
    @JsonProperty(value = "upperCorner")
    val upperCorner: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Point(
    @JsonProperty(value = "pos")
    val pos: String
)
