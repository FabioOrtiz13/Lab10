package com.tecsup.lab10.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tecsup.lab10.data.SerieApiService
import com.tecsup.lab10.data.SerieModel
import kotlinx.coroutines.delay

@Composable
fun ContenidoSeriesListado(navController: NavHostController, servicio: SerieApiService) {
    var listaSeries: SnapshotStateList<SerieModel> = remember { mutableStateListOf() }

    LaunchedEffect(Unit) {
        val listado = servicio.selectSeries()
        listado.forEach { listaSeries.add(it) }
    }

    LazyColumn {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.1f)
                )
                Text(
                    text = "SERIE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.7f)
                )
                Text(
                    text = "Acción",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.2f)
                )
            }
        }

        items(listaSeries) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.id}",
                    fontSize = 18.sp,
                    modifier = Modifier.weight(0.1f)
                )
                Text(
                    text = item.name,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(0.6f)
                )
                IconButton(
                    onClick = {
                        navController.navigate("serieVer/${item.id}")
                        Log.e("SERIE-VER", "ID = ${item.id}")
                    },
                    modifier = Modifier.weight(0.1f)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Editar")
                }
                IconButton(
                    onClick = {
                        navController.navigate("serieDel/${item.id}")
                        Log.e("SERIE-DEL", "ID = ${item.id}")
                    },
                    modifier = Modifier.weight(0.1f)
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}

@Composable
fun ContenidoSerieEditar(navController: NavHostController, servicio: SerieApiService, pid: Int = 0) {
    var id by remember { mutableStateOf(pid) }
    var name by remember { mutableStateOf("") }
    var releaseDate by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var grabar by remember { mutableStateOf(false) }

    if (id != 0) {
        LaunchedEffect(Unit) {
            val objSerie = servicio.selectSerie(id.toString())
            delay(100)
            objSerie.body()?.let {
                name = it.name
                releaseDate = it.release_date
                rating = it.rating.toString()
                category = it.category
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        TextField(
            value = id.toString(),
            onValueChange = { },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = releaseDate,
            onValueChange = { releaseDate = it },
            label = { Text("Release Date") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = rating,
            onValueChange = { rating = it },
            label = { Text("Rating") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { grabar = true }) {
            Text("Grabar", fontSize = 16.sp)
        }
    }

    if (grabar) {
        val objSerie = SerieModel(id, name, releaseDate, rating.toInt(), category)
        LaunchedEffect(Unit) {
            if (id == 0)
                servicio.insertSerie(objSerie)
            else
                servicio.updateSerie(id.toString(), objSerie)
        }
        grabar = false
        navController.navigate("series")
    }
}

@Composable
fun ContenidoSerieEliminar(navController: NavHostController, servicio: SerieApiService, id: Int) {
    var showDialog by remember { mutableStateOf(true) }
    var borrar by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Está seguro de eliminar la Serie?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    borrar = true
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                    navController.navigate("series")
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (borrar) {
        LaunchedEffect(Unit) {
            servicio.deleteSerie(id.toString())
            borrar = false
            navController.navigate("series")
        }
    }
}
