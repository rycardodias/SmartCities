package ipvc.estg.smartcities.api

data class MapIncidences(
    val id: Int,
    val users_id: Int,
    val latCoordinates: Double,
    val longCoordinates: Double,
    val title: String,
    val description: String,
    val image: String,
    val carTrafficProblem: Int,
    val solved: Int
)
