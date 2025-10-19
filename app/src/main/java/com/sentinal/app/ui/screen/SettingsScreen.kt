package com.sentinal.app.ui.screen

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.service.GuardianService
import com.sentinal.app.ui.Routes
import com.sentinal.app.util.Curfew
import com.sentinal.app.util.ProfilePrefs

@Composable
fun SettingsScreen(nav: NavController) {
    val ctx = LocalContext.current

    var c1Name by remember { mutableStateOf(ProfilePrefs.getContact1Name(ctx)) }
    var c1Num  by remember { mutableStateOf(ProfilePrefs.getContact1Number(ctx)) }
    var c2Name by remember { mutableStateOf(ProfilePrefs.getContact2Name(ctx)) }
    var c2Num  by remember { mutableStateOf(ProfilePrefs.getContact2Number(ctx)) }

    var autoArm by remember { mutableStateOf(ProfilePrefs.isAutoArm(ctx)) }
    var theme by remember { mutableStateOf(ProfilePrefs.getTheme(ctx)) }
    var guardianOn by remember { mutableStateOf(false) }

    var pickingSlot by remember { mutableStateOf(1) }
    val pickContact = rememberLauncherForActivityResult(ActivityResultContracts.PickContact()) { uri: Uri? ->
        if (uri != null) {
            val (name, number) = queryNameNumber(ctx, uri)
            if (!name.isNullOrBlank() && !number.isNullOrBlank()) {
                val sanitized = sanitizePhone(number)
                if (pickingSlot == 1) { c1Name = name; c1Num = sanitized } else { c2Name = name; c2Num = sanitized }
            }
        }
    }
    val permissionReq = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) pickContact.launch(null)
    }
    fun askToPick(slot: Int) { pickingSlot = slot; permissionReq.launch(android.Manifest.permission.READ_CONTACTS) }

    var curfewEnabled by remember { mutableStateOf(ProfilePrefs.isCurfewEnabled(ctx)) }
    var curfewHour by remember { mutableStateOf(ProfilePrefs.getCurfewHour(ctx)) }
    var curfewMin  by remember { mutableStateOf(ProfilePrefs.getCurfewMin(ctx)) }
    var daysMask   by remember { mutableStateOf(ProfilePrefs.getCurfewDaysMask(ctx)) }
    fun toggleDay(bit: Int) { daysMask = daysMask xor (1 shl bit) }

    var trustedCount by remember { mutableStateOf(ProfilePrefs.getTrustedPlaces(ctx).size) }

    val scroll = rememberScrollState()

    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }) }) { pad ->
        Column(
            Modifier
                .padding(pad)
                .navigationBarsPadding()
                .imePadding()
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Emergency Contacts", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(c1Name, { c1Name = it }, label = { Text("Contact #1 name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = c1Num, onValueChange = { c1Num = it },
                    label = { Text("Contact #1 phone (SMS)") }, singleLine = true, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                OutlinedButton(onClick = { askToPick(1) }, modifier = Modifier.widthIn(min = 110.dp)) { Text("Pick") }
            }
            OutlinedTextField(c2Name, { c2Name = it }, label = { Text("Contact #2 name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = c2Num, onValueChange = { c2Num = it },
                    label = { Text("Contact #2 phone (SMS)") }, singleLine = true, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                OutlinedButton(onClick = { askToPick(2) }, modifier = Modifier.widthIn(min = 110.dp)) { Text("Pick") }
            }
            Button(onClick = { ProfilePrefs.setContacts(ctx, c1Name, c1Num, c2Name, c2Num) }) { Text("Save Contacts") }

            HorizontalDivider()

            Text("Background Protection", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = guardianOn, onCheckedChange = {
                    guardianOn = it
                    if (it) GuardianService.start(ctx) else GuardianService.stop(ctx)
                })
                Spacer(Modifier.width(12.dp))
                Text(if (guardianOn) "On (foreground service running)" else "Off")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { openBatterySettings(ctx) }) { Text("Fix Battery Optimization") }
                if (Build.VERSION.SDK_INT >= 33) {
                    Button(onClick = { requestNotifPermission(ctx) }) { Text("Allow Notifications") }
                }
            }

            HorizontalDivider()

            Text("Curfew (Teen)", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = curfewEnabled, onCheckedChange = {
                    curfewEnabled = it
                    ProfilePrefs.setCurfewEnabled(ctx, it)
                    if (it) Curfew.scheduleNext(ctx) else Curfew.cancel(ctx)
                })
                Spacer(Modifier.width(12.dp))
                Text(if (curfewEnabled) "Enabled" else "Disabled")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = curfewHour.toString(), onValueChange = { v -> curfewHour = v.toIntOrNull()?.coerceIn(0,23) ?: curfewHour },
                    label = { Text("Hour (0–23)") }, singleLine = true, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = curfewMin.toString(), onValueChange = { v -> curfewMin = v.toIntOrNull()?.coerceIn(0,59) ?: curfewMin },
                    label = { Text("Min (0–59)") }, singleLine = true, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Button(onClick = {
                    ProfilePrefs.setCurfewTime(ctx, curfewHour, curfewMin)
                    if (curfewEnabled) Curfew.scheduleNext(ctx)
                }) { Text("Save Time") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DayChip("Sun", 0, daysMask) { toggleDay(0) }
                DayChip("Mon", 1, daysMask) { toggleDay(1) }
                DayChip("Tue", 2, daysMask) { toggleDay(2) }
                DayChip("Wed", 3, daysMask) { toggleDay(3) }
                DayChip("Thu", 4, daysMask) { toggleDay(4) }
                DayChip("Fri", 5, daysMask) { toggleDay(5) }
                DayChip("Sat", 6, daysMask) { toggleDay(6) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    ProfilePrefs.setCurfewDaysMask(ctx, daysMask)
                    if (curfewEnabled) Curfew.scheduleNext(ctx)
                }) { Text("Save Days") }
                OutlinedButton(onClick = { ProfilePrefs.skipCurfewToday(ctx) }) { Text("Skip tonight") }
                OutlinedButton(onClick = { Curfew.fireTest(ctx) }) { Text("Test Curfew Alert") }
            }

            HorizontalDivider()

            Text("Safety & Geofence", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = autoArm, onCheckedChange = {
                    autoArm = it
                    ProfilePrefs.setAutoArm(ctx, it)
                })
                Spacer(Modifier.width(12.dp))
                Text("Auto-arm AI Guardian outside trusted geofence")
            }
            AssistChip(
                onClick = {
                    nav.navigate(Routes.TRUSTED_PLACES)
                },
                label = { Text("Trusted Places (${trustedCount})") }
            )

            HorizontalDivider()

            Text("Appearance", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(selected = theme == "system", onClick = { theme = "system"; ProfilePrefs.setTheme(ctx, "system") }, label = { Text("System") })
                FilterChip(selected = theme == "light",  onClick = { theme = "light";  ProfilePrefs.setTheme(ctx, "light")  }, label = { Text("Light") })
                FilterChip(selected = theme == "dark",   onClick = { theme == "dark";   ProfilePrefs.setTheme(ctx, "dark")   }, label = { Text("Dark") })
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DayChip(text: String, bit: Int, mask: Int, onToggle: () -> Unit) {
    FilterChip(selected = (mask and (1 shl bit)) != 0, onClick = onToggle, label = { Text(text) })
}

private fun queryNameNumber(ctx: Context, uri: Uri): Pair<String?, String?> {
    val nameProjection = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME)
    var contactId: String? = null
    var displayName: String? = null

    ctx.contentResolver.query(uri, nameProjection, null, null, null)?.use { c: Cursor ->
        if (c.moveToFirst()) {
            val idIdx = c.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIdx = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            if (idIdx >= 0) contactId = c.getString(idIdx)
            if (nameIdx >= 0) displayName = c.getString(nameIdx)
        }
    }
    ctx.contentResolver.query(uri, arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER), null, null, null)?.use { p ->
        if (p.moveToFirst()) {
            val numIdx = p.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            if (numIdx >= 0) {
                val number = p.getString(numIdx)
                if (!number.isNullOrBlank()) return displayName to number
            }
        }
    }
    val number = if (contactId != null) {
        var chosen: String? = null
        val sel = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID}=?"
        val args = arrayOf(contactId!!)
        val phoneProjection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.IS_PRIMARY
        )
        ctx.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            phoneProjection, sel, args, null
        )?.use { ph ->
            while (ph.moveToNext()) {
                val numIdx = ph.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val priIdx = ph.getColumnIndex(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY)
                val num = if (numIdx >= 0) ph.getString(numIdx) else null
                val isPrimary = if (priIdx >= 0) ph.getInt(priIdx) == 1 else false
                if (isPrimary && !num.isNullOrBlank()) { chosen = num; break }
                if (chosen == null && !num.isNullOrBlank()) chosen = num
            }
        }
        chosen
    } else null
    return displayName to number
}
private fun sanitizePhone(s: String): String = s.filter { it.isDigit() || it == '+' }

private fun openBatterySettings(ctx: Context) {
    val i = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { ctx.startActivity(i) }
}
private fun requestNotifPermission(ctx: Context) {
    val i = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { ctx.startActivity(i) }
}
