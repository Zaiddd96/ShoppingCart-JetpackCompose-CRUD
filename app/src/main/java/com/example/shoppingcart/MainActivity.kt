package com.example.shoppingcart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoppingcart.ui.theme.ShoppingCartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppingCartTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    ShoppingCart(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// Datatype of items in the Cart
data class ShoppingItems(
    val id:Int,
    var name:String,
    var quantity:Int,
    var isEditing:Boolean = false
)

@Composable
fun Logo(){
    Image(
        painter = painterResource(id = R.drawable.cart),
        contentDescription = "Shopping Cart logo",
        modifier = Modifier.padding(start = 42.dp).size(300.dp)
    )
}

@Composable
//Main Shopping Cart
fun ShoppingCart(modifier: Modifier = Modifier){
    var sItems by remember {
        mutableStateOf(listOf<ShoppingItems>())
    }
    var isDialog by remember {
        mutableStateOf(false)
    }
    var item by remember {
        mutableStateOf(" ")
    }
    var quantity by remember {
        mutableStateOf("1")
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
        )
    {
//        val gradientColors = listOf(
//            Color(0xFF9E9D24),
//            Color(0xFF80E6B8)
//        )
//        Text(
//            text = "Shopping Cart\uD83D\uDED2", fontWeight = FontWeight.ExtraBold,
//            fontSize = 40.sp,
//            style = TextStyle(brush = Brush.linearGradient(gradientColors)) ,
//            modifier = Modifier
//                .align(Alignment.CenterHorizontally)
//                .padding(40.dp)
//        )
        Logo()

        ElevatedButton(onClick = { isDialog = true },
            shape = CutCornerShape(4.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
            ) {
            Text(text = "Add To Cart")

        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(sItems){
                item ->
                if (item.isEditing){
                    CartEditor(
                        item = item,
                        onEditComplete = {
                            editedname, editedquantity ->
                            sItems = sItems.map { it.copy(isEditing = false) }
                            val editedItems = sItems.find { item.id == it.id }
                            editedItems?.let {
                                it.name = editedname
                                it.quantity = editedquantity
                            }
                        }
                    )
                }
                else{
                    ShoppingCartItem(cart = item,
                        onEdit = {
                            sItems = sItems.map { it.copy(isEditing = it.id == item.id) }
                        },
                        onDelete = {
                            sItems = sItems - item
                        }
                    )
                }

            }
        }
    }
    if (isDialog){
        AlertDialog(
            onDismissRequest = { isDialog = false },
            title = { Text(text = "Add items to Cart!")},
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { isDialog = false }
                    ) {
                        Text(text = "Cancel")
                    }
                    Button(
                        onClick = {
                            isDialog = false
                            if (item.isNotBlank()){
                                val shoppingitem = ShoppingItems(
                                    id = sItems.size+1,
                                    name = item,
                                    quantity = quantity.toIntOrNull() ?: 1
                                )
                                sItems = sItems + shoppingitem
                                item = " "
                                quantity = "1"
                            }

                        }
                    ) {
                        Text(text = "Add")
                    }

                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = item,
                        onValueChange = { item = it },
                        label = { Text(text = "Add item:") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                    OutlinedTextField(
                        value = quantity.toString(),
                        onValueChange = { quantity = it },
                        label = { Text(text = "Quantity:") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                }
            }

        )
    }
}


@Composable
// Cart items which will be display on the ui and added in the list
fun ShoppingCartItem(
    cart:ShoppingItems,
    onEdit: ()-> Unit,
    onDelete: ()-> Unit
) {
    var confirmDialog by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(
                    2.dp,
                    if (isSystemInDarkTheme()) {
                        Color.Cyan
                    } else {
                        Color.Black
                    }
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = cart.name, modifier = Modifier.padding(10.dp),
            color = if (isSystemInDarkTheme()){
                Color(0xFF9E9D24)
            }
            else{
                Color.Black
            }
            )
        Text(text = "Qty:${cart.quantity}", modifier = Modifier.padding(8.dp),
            color = if (isSystemInDarkTheme()){
                Color(0xFF9E9D24)
            }
            else{
                Color.Black
            }
            )
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            IconButton(onClick = { onEdit() }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null,
                    tint = if (isSystemInDarkTheme()){
                        Color(0xFF9E9D24)
                    }
                    else{
                        Color.Black
                    }
                )
            }
            IconButton(onClick = { confirmDialog = true }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null,
                    tint = if (isSystemInDarkTheme()){
                        Color(0xFF9E9D24)
                    }
                    else{
                        Color.Black
                    }
                    )
            }
        }
        if (confirmDialog){
            AlertDialog(
                onDismissRequest = { confirmDialog = false },
                confirmButton = {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Are you sure? This action can’t be undone!")
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ElevatedButton(onClick = {
                                confirmDialog = false
                                onDelete()
                            }) {
                                Text(text = "Confirm")
                            }
                            ElevatedButton(onClick = {
                                confirmDialog = false
                            }) {
                                Text(text = "Cancel")
                            }
                        }
                    }
                })
        }
    }
}
 
@Composable
//To edit the existing Cart
fun CartEditor(
    item:ShoppingItems,
    onEditComplete:(String,Int) -> Unit
){
    var editName by remember {
        mutableStateOf(item.name)
    }
    var editQuantity by remember {
        mutableStateOf(item.quantity.toString())
    }
    var isEditing by remember {
        mutableStateOf(item.isEditing)
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.Transparent)
        .padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        Column {
            BasicTextField(
                value = editName,
                onValueChange = {editName = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
            BasicTextField(
                value = editQuantity,
                onValueChange = {editQuantity = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
        }
        ElevatedButton(onClick = {
            isEditing = false
            onEditComplete(editName, editQuantity.toIntOrNull()?:1)
        }
        ) {
            Text(text = "Commit")
        }
    }

}


@Preview(showBackground = true)
@Composable
//Preview the Application
fun ShoppingCartPreview(){
    ShoppingCart()
}
