package com.example.theshoppinglistapp

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext

data class ShoppingItem(val id : Int, var name : String, var quantity : Int, var isEditing : Boolean = false)

@Composable
fun ShoppingListApp(){
    var listOfItems by remember { mutableStateOf(listOf<ShoppingItem>())}
    var showAddWindow : Boolean by remember { mutableStateOf(false) }
    var itemName : String by remember { mutableStateOf("") }
    var itemQuantity : String by remember { mutableStateOf("1") }
    val context = LocalContext.current  //for Toast functionality

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ){
        IconButton(
            onClick = { showAddWindow = true },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(0.dp, 32.dp, 0.dp, 0.dp)
                .background(Color(0xFF495D91), shape = CircleShape)
        ) {
            val DoubleNumber : (Int) -> Int = {it * 2}
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Plus Symbol",
                tint = Color.White
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ){
            items(listOfItems){
                ShoppingListItem(it, {}, {})
            }
        }
    }

    if (showAddWindow){
        AlertDialog(
            onDismissRequest = { showAddWindow = false },
            title = {Text(text = "Add Item")},
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = {
                            if (it.length <= 16) {
                                itemName = it
                            }else{
                                Toast.makeText(context, "Max character limit reached!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        singleLine = true,
                        label = { Text("Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = {
                            if (it != ""){
                                if (it <= "2147483647") {
                                    itemQuantity = it
                                }else{
                                    Toast.makeText(context, "Max number limit reached!", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(context, "Number must be added!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        singleLine = true,
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    Button(onClick = {
                        if (itemName.isNotBlank()){
                            val newItem = ShoppingItem(
                                id = listOfItems.size + 1,
                                name = itemName,
                                quantity = itemQuantity.toInt()
                            )
                            listOfItems = listOfItems + newItem
                            showAddWindow = false
                            itemName = ""   //erases value, user filled up, so next time there'll be empty field
                            itemQuantity = "1"
                            Toast.makeText(context, "New item has been added!", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text(text = "Add")
                    }
                    Button(onClick = {
                        showAddWindow = false
                        Toast.makeText(context, "Action has been cancelled!", Toast.LENGTH_SHORT).show()
                    }) {
                        Text(text = "Cancel")
                    }
                }
            }
        )
    }
}

@Composable
fun ShoppingListItemEditor(
    item : ShoppingItem,
    onEditComplete : (String, Int) -> Unit
){
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        Column (
            modifier = Modifier
                .padding(8.dp)
        ) {
            BasicTextField(
                value = editedName,
                onValueChange = {editedName = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
            BasicTextField(
                value = editedQuantity,
                onValueChange = {editedQuantity = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
        }
        Button(
            onClick = {
                isEditing = false
                onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)
            }
        ) {
            Text(text = "Save")
        }
    }
}

@Composable
fun ShoppingListItem(
    item : ShoppingItem,
    onEditClick : () -> Unit,   //no input or output === () and UNIT
    onDeleteClick : () -> Unit
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color(0xFF495D91)),
                shape = RoundedCornerShape(20)
            )
    ){
        Text(
            text = item.name,
            modifier = Modifier
                .padding(8.dp)
        )
        Text(
            text = "Qty: ${item.quantity}",

            modifier = Modifier.padding(8.dp)
        )
        Row (
            modifier = Modifier
                .padding(8.dp)
        ){
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Symbol",
                    tint = Color.Black
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Symbol",
                    tint = Color.Black
                )
            }
        }
    }
}