package ipvc.estg.smartcities.api

data class MapIncidences(
    val id: Int,
    val user_id: Int,
    val latCoordinates: String,
    val longCoordinates: String,
    val title: String,
    val description: String,
    val image: String,
    val carTrafficProblem: Int,
    val solved: Int
)
