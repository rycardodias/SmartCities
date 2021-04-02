package ipvc.estg.smartcities.api

data class MapIncidences(
    val id: Int,
    val users_id: Int,
    val latCoordinates: String,
    val longCoordinates: String,
    val title: String,
    val description: String,
    val image: String,
    val carTrafficProblem: Int,
    val solved: Int
)
