package edu.itesm.tournamentHelp

data class Results(
    var data: Data
)

data class Data(
    var results : MutableList<Comic>
)

data class Comic(
    val title: String,
    var description: String,

)

