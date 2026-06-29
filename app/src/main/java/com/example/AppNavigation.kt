package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

// --- Route Definition ---
sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Market : Screen("market", "Market", Icons.Default.TrendingUp)
    object AIAdvisor : Screen("ai_advisor", "AI Advisor", Icons.Default.SmartToy)
    object MyProfile : Screen("my_profile", "My Profile", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    tokenViewModel: TokenViewModel = viewModel(),
    chatViewModel: ChatViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "App Icon Logo",
                            tint = CyberGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "WARERA // COMMAND",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = CyberGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MilitaryBlack,
                    titleContentColor = CyberGreen
                ),
                actions = {
                    // Quick stats indicator
                    val power by tokenViewModel.militaryPower.collectAsState()
                    Surface(
                        color = TacticalOlive,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .border(1.dp, BorderGreen, RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            text = "PWR: ${power / 1000}K",
                            color = CyberGreen,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                },
                modifier = Modifier.border(0.dp, BorderGreen).drawBehind {
                    // Cyberpunk bottom divider border line
                    drawLine(
                        color = BorderGreen,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2f
                    )
                }
            )
        },
        bottomBar = {
            val items = listOf(Screen.Market, Screen.AIAdvisor, Screen.MyProfile)
            NavigationBar(
                containerColor = GunmetalGray,
                tonalElevation = 8.dp,
                modifier = Modifier.drawBehind {
                    // Cyberpunk top border for NavigationBar
                    drawLine(
                        color = BorderGreen,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 2f
                    )
                }
            ) {
                items.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                imageVector = screen.icon, 
                                contentDescription = screen.title,
                                tint = if (selected) CyberGreen else MutedText
                            ) 
                        },
                        label = { 
                            Text(
                                text = screen.title,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) CyberGreen else MutedText
                            ) 
                        },
                        selected = selected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberGreen,
                            unselectedIconColor = MutedText,
                            indicatorColor = TacticalOlive
                        ),
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.testTag("nav_tab_${screen.route}")
                    )
                }
            }
        },
        containerColor = MilitaryBlack
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Market.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Market.route) {
                MarketScreen()
            }
            composable(Screen.AIAdvisor.route) {
                AIAdvisorScreen(tokenViewModel, chatViewModel)
            }
            composable(Screen.MyProfile.route) {
                MyProfileScreen(tokenViewModel)
            }
        }
    }
}

// ==========================================
// 1. MARKET SCREEN
// ==========================================
@Composable
fun MarketScreen() {
    var ironPrice by remember { mutableStateOf(142.5f) }
    var foodPrice by remember { mutableStateOf(85.2f) }
    var goldPrice by remember { mutableStateOf(1048.0f) }
    
    var ironTrend by remember { mutableStateOf(2.4f) }
    var foodTrend by remember { mutableStateOf(-1.2f) }
    var goldTrend by remember { mutableStateOf(0.8f) }
    
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Simulated historical data points for custom canvas rendering
    val historyPoints = remember { mutableStateListOf(120f, 135f, 110f, 150f, 140f, 160f, 142f) }

    fun simulateRefresh() {
        scope.launch {
            isRefreshing = true
            delay(800) // Tactical loading delay
            
            // Random fluctuations
            val ironDelta = Random.nextFloat() * 10 - 5
            val foodDelta = Random.nextFloat() * 6 - 3
            val goldDelta = Random.nextFloat() * 40 - 20
            
            ironPrice = (ironPrice + ironDelta).coerceIn(90f, 250f)
            foodPrice = (foodPrice + foodDelta).coerceIn(50f, 150f)
            goldPrice = (goldPrice + goldDelta).coerceIn(800f, 1500f)
            
            ironTrend = ironDelta
            foodTrend = foodDelta
            goldTrend = goldDelta

            // Add new simulated point to our historical chart
            historyPoints.removeAt(0)
            historyPoints.add(ironPrice.coerceIn(100f, 200f))
            
            isRefreshing = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HUD Grid Header
        item {
            Column {
                Text(
                    text = "[LIVE WARERA COMMODITIES INDEX]",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberGreen,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Operational sector trade feeds are synchronized. Tactical arbitrage grids active.",
                    fontFamily = FontFamily.Default,
                    color = MutedText,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Tactical Price Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ResourceCard(
                    name = "IRON ORE",
                    price = String.format("%.1f", ironPrice),
                    trend = ironTrend,
                    unit = "ton",
                    modifier = Modifier.weight(1f)
                )
                ResourceCard(
                    name = "RATIONS",
                    price = String.format("%.1f", foodPrice),
                    trend = foodTrend,
                    unit = "crate",
                    modifier = Modifier.weight(1f)
                )
                ResourceCard(
                    name = "HYPER GOLD",
                    price = String.format("%,.0f", goldPrice),
                    trend = goldTrend,
                    unit = "bar",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Custom Canvas Visualizer (Visual Asset Polish)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GunmetalGray),
                border = BorderStroke(1.dp, BorderGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "MARKET FLUCTUATION VECTOR: IRON RATE",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = CyberGreenMuted,
                        fontSize = 10.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Draw custom glowing military grid chart on Canvas
                    Box(modifier = Modifier.fillMaxSize()) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height
                            
                            // Draw horizontal grid lines
                            val gridLines = 4
                            for (i in 0..gridLines) {
                                val y = (height / gridLines) * i
                                drawLine(
                                    color = BorderGreen.copy(alpha = 0.2f),
                                    start = Offset(0f, y),
                                    end = Offset(width, y),
                                    strokeWidth = 1f
                                )
                            }
                            
                            // Map resource price trends onto Path
                            val maxVal = 200f
                            val minVal = 80f
                            val valRange = maxVal - minVal
                            
                            val points = historyPoints.mapIndexed { idx, price ->
                                val x = (width / (historyPoints.size - 1)) * idx
                                val normalizedY = (price - minVal) / valRange
                                val y = height - (normalizedY * height)
                                Offset(x, y)
                            }
                            
                            val path = Path().apply {
                                points.forEachIndexed { index, offset ->
                                    if (index == 0) {
                                        moveTo(offset.x, offset.y)
                                    } else {
                                        lineTo(offset.x, offset.y)
                                    }
                                }
                            }
                            
                            // Draw line
                            drawPath(
                                path = path,
                                color = CyberGreen,
                                style = Stroke(width = 4f)
                            )
                            
                            // Draw nodes/points
                            points.forEach { offset ->
                                drawCircle(
                                    color = CyberGreen,
                                    radius = 6f,
                                    center = offset
                                )
                                drawCircle(
                                    color = CyberGreen.copy(alpha = 0.3f),
                                    radius = 12f,
                                    center = offset
                                )
                            }
                        }
                    }
                }
            }
        }

        // Arbitrage Vectors Panel
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGreen, RoundedCornerShape(4.dp))
                    .background(CardBackground)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning Info",
                        tint = WarningAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "COGNITIVE ARBITRAGE SIGNALS",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = WarningAmber,
                        fontSize = 12.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                ArbitrageItem(
                    title = "FOOD SQUEEZE - OUTPOST DELTA",
                    desc = "Purchase Food at sector depot, ship to Sector 9. High margin opportunity.",
                    yield = "+6.2% Yield"
                )
                Divider(color = BorderGreen.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))
                ArbitrageItem(
                    title = "IRON DEFICIT - CRUCIBLE SEC-4",
                    desc = "Iron is heavily undervalued locally. Hold assets for next 12 cycles.",
                    yield = "HOLD Recommendation"
                )
            }
        }

        // Action Buttons
        item {
            Button(
                onClick = { simulateRefresh() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TacticalOlive,
                    contentColor = CyberGreen
                ),
                border = BorderStroke(1.dp, CyberGreen),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("refresh_market_button"),
                enabled = !isRefreshing
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = CyberGreen,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SYNCHRONIZING READOUTS...",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync icon"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FORCE TRANS-PONDER SYNC",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ResourceCard(
    name: String,
    price: String,
    trend: Float,
    unit: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GunmetalGray),
        border = BorderStroke(1.dp, BorderGreen),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                fontFamily = FontFamily.Monospace,
                color = MutedText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = price,
                fontFamily = FontFamily.Monospace,
                color = BrightText,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
            
            Text(
                text = "credits / $unit",
                color = MutedText,
                fontSize = 9.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            val isPositive = trend >= 0
            val trendText = if (isPositive) "+${String.format("%.1f", trend)}%" else "${String.format("%.1f", trend)}%"
            val trendColor = if (isPositive) CyberGreen else AlertRed

            Text(
                text = trendText,
                fontFamily = FontFamily.Monospace,
                color = trendColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ArbitrageItem(
    title: String,
    desc: String,
    yield: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = BrightText,
                fontSize = 11.sp
            )
            Text(
                text = yield,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = CyberGreen,
                fontSize = 11.sp
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = desc,
            color = MutedText,
            fontSize = 11.sp
        )
    }
}

// ==========================================
// 2. AI ADVISOR SCREEN
// ==========================================
@Composable
fun AIAdvisorScreen(
    tokenViewModel: TokenViewModel,
    chatViewModel: ChatViewModel
) {
    val messages by chatViewModel.messages.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()
    val apiToken by tokenViewModel.apiToken.collectAsState()
    val playerName by tokenViewModel.playerName.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val isKeyConfigured = remember {
        val key = BuildConfig.GEMINI_API_KEY
        key.isNotBlank() && key != "MY_GEMINI_API_KEY" && !key.contains("placeholder", ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // AI Connection Banner Status
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (isKeyConfigured) BorderGreen else WarningAmber, RoundedCornerShape(4.dp))
                .background(if (isKeyConfigured) TacticalOlive.copy(alpha = 0.5f) else WarningAmber.copy(alpha = 0.1f))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (isKeyConfigured) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = "Status Key Icon",
                tint = if (isKeyConfigured) CyberGreen else WarningAmber,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(
                    text = if (isKeyConfigured) "GEMINI ADVISOR: QUANTUM COUPLING READY" else "GEMINI ADVISOR: LOCAL SIMULATOR RUNNING",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = if (isKeyConfigured) CyberGreen else WarningAmber
                )
                Text(
                    text = if (isKeyConfigured) "Connected directly to Gemini neural cores." else "Offline state engines simulating strategic advice.",
                    fontSize = 11.sp,
                    color = MutedText
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Message Feed Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, BorderGreen, RoundedCornerShape(4.dp))
                .background(MilitaryBlack)
                .padding(8.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    ChatMessageItem(message)
                }
                
                if (isLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = CyberGreen,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "CALCULATING TACTICAL STRATAGEMS...",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = CyberGreen
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Suggestion Prompts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Combat Fleet Setup", "Market Arbitrage", "Upgrade Plan").forEach { prompt ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, BorderGreen, RoundedCornerShape(12.dp))
                        .clickable {
                            focusManager.clearFocus()
                            chatViewModel.sendMessage(prompt, apiToken, playerName)
                        }
                        .background(TacticalOlive)
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = prompt,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberGreen,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chat Input Area
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = textInput,
                onValueChange = { textInput = it },
                textStyle = LocalTextStyle.current.copy(
                    color = BrightText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                ),
                placeholder = {
                    Text(
                        text = "TRANSMIT COMMAND TO ADVISOR...",
                        color = MutedText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, BorderGreen, RoundedCornerShape(4.dp))
                    .testTag("chat_input_field"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = CyberGreen
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (textInput.isNotBlank()) {
                        focusManager.clearFocus()
                        chatViewModel.sendMessage(textInput, apiToken, playerName)
                        textInput = ""
                    }
                })
            )

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(TacticalOlive)
                    .border(1.dp, CyberGreen, RoundedCornerShape(4.dp))
                    .clickable {
                        if (textInput.isNotBlank()) {
                            focusManager.clearFocus()
                            chatViewModel.sendMessage(textInput, apiToken, playerName)
                            textInput = ""
                        }
                    }
                    .testTag("send_chat_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Chat Key",
                    tint = CyberGreen
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val isAdvisor = message.sender == "ADVISOR"
    
    val borderStroke = if (isAdvisor) {
        BorderStroke(1.dp, BorderGreen)
    } else {
        BorderStroke(1.dp, BorderGreen.copy(alpha = 0.5f))
    }

    val titleColor = if (isAdvisor) CyberGreen else BrightText
    val titleTag = if (isAdvisor) "VANGUARD ADVISOR // FEED" else "PLAYER COMMANDER // FEED"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(borderStroke, RoundedCornerShape(4.dp))
            .background(
                color = if (isAdvisor) CardBackground else TacticalOlive.copy(alpha = 0.4f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(10.dp)
    ) {
        Text(
            text = titleTag,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = titleColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message.text,
            fontFamily = if (isAdvisor) FontFamily.Monospace else FontFamily.Default,
            fontSize = 13.sp,
            color = if (isAdvisor) TerminalText else BrightText,
            lineHeight = 18.sp
        )
    }
}

// ==========================================
// 3. MY PROFILE SCREEN
// ==========================================
@Composable
fun MyProfileScreen(tokenViewModel: TokenViewModel) {
    val apiToken by tokenViewModel.apiToken.collectAsState()
    val playerName by tokenViewModel.playerName.collectAsState()
    val faction by tokenViewModel.faction.collectAsState()
    val militaryPower by tokenViewModel.militaryPower.collectAsState()
    val allianceTag by tokenViewModel.allianceTag.collectAsState()
    val coordinates by tokenViewModel.coordinates.collectAsState()

    var tokenInput by remember { mutableStateOf(apiToken) }
    var isEditingProfile by remember { mutableStateOf(false) }

    // Form editing states
    var editName by remember { mutableStateOf(playerName) }
    var editFaction by remember { mutableStateOf(faction) }
    var editPower by remember { mutableStateOf(militaryPower.toString()) }
    var editTag by remember { mutableStateOf(allianceTag) }
    var editCoords by remember { mutableStateOf(coordinates) }

    var isTokenVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current

    // Sync state if saved in Viewmodel
    LaunchedEffect(apiToken) {
        tokenInput = apiToken
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Elite Cyberbadge Profile visual header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GunmetalGray),
                border = BorderStroke(1.dp, BorderGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tactical badge graphic
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .border(2.dp, CyberGreen, RoundedCornerShape(8.dp))
                            .background(TacticalOlive),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(48.dp)) {
                            // Military star pattern on HUD avatar
                            val path = Path().apply {
                                val cx = size.width / 2f
                                val cy = size.height / 2f
                                moveTo(cx, 0f)
                                lineTo(cx * 1.3f, cy * 0.7f)
                                lineTo(size.width, cy)
                                lineTo(cx * 1.3f, cy * 1.3f)
                                lineTo(cx, size.height)
                                lineTo(cx * 0.7f, cy * 1.3f)
                                lineTo(0f, cy)
                                lineTo(cx * 0.7f, cy * 0.7f)
                                close()
                            }
                            drawPath(path, CyberGreen, style = Stroke(width = 3f))
                            drawCircle(CyberGreen, radius = 4f)
                        }
                    }

                    Column {
                        Text(
                            text = playerName.uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = BrightText,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "ALLIANCE: [$allianceTag] // FACTION: $faction",
                            fontFamily = FontFamily.Monospace,
                            color = CyberGreenMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "SECTOR COORDS: $coordinates",
                            fontFamily = FontFamily.Monospace,
                            color = MutedText,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // WarEra API Token Security Setup Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, BorderGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Token Key",
                            tint = CyberGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "WARERA SECURE API CREDENTIALS",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyberGreen,
                            fontSize = 12.sp
                        )
                    }
                    
                    Text(
                        text = "Add your personal WarEra.io token to unlock automated live sync of coordinates, legion power indices, and tactical trade balances.",
                        fontSize = 11.sp,
                        color = MutedText
                    )

                    TextField(
                        value = tokenInput,
                        onValueChange = { tokenInput = it },
                        textStyle = LocalTextStyle.current.copy(
                            color = BrightText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        ),
                        placeholder = {
                            Text(
                                text = "ENTER WARERA_API_TOKEN...",
                                color = MutedText.copy(alpha = 0.5f),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderGreen, RoundedCornerShape(4.dp))
                            .testTag("api_token_input_field"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MilitaryBlack,
                            unfocusedContainerColor = MilitaryBlack,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = CyberGreen
                        ),
                        visualTransformation = if (isTokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                IconButton(onClick = {
                                    val text = clipboardManager.getText()?.text
                                    if (!text.isNullOrBlank()) {
                                        tokenInput = text
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ContentPaste,
                                        contentDescription = "Paste from clipboard",
                                        tint = CyberGreen
                                    )
                                }
                                IconButton(onClick = { isTokenVisible = !isTokenVisible }) {
                                    Icon(
                                        imageVector = if (isTokenVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle token visibility",
                                        tint = MutedText
                                    )
                                }
                            }
                        },
                        singleLine = true
                    )

                    // Helper Paste Button
                    Button(
                        onClick = {
                            val text = clipboardManager.getText()?.text
                            if (!text.isNullOrBlank()) {
                                tokenInput = text
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TacticalOlive.copy(alpha = 0.5f),
                            contentColor = CyberGreen
                        ),
                        border = BorderStroke(1.dp, BorderGreen),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Paste Clipboard Icon",
                            modifier = Modifier.size(16.dp),
                            tint = CyberGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PASTE FROM DEVICE CLIPBOARD",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                tokenViewModel.saveToken(tokenInput)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TacticalOlive,
                                contentColor = CyberGreen
                            ),
                            border = BorderStroke(1.dp, CyberGreen),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("save_token_button")
                        ) {
                            Text(
                                text = "SAVE DECRYPT KEY",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        if (apiToken.isNotBlank()) {
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    tokenInput = ""
                                    tokenViewModel.clearToken()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = AlertRed
                                ),
                                border = BorderStroke(1.dp, AlertRed),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "WIPE KEY",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Editable Commander Profile Form
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GunmetalGray),
                border = BorderStroke(1.dp, BorderGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "COMMAND PROTOCOL CONFIG",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = BrightText,
                            fontSize = 12.sp
                        )
                        
                        Text(
                            text = if (isEditingProfile) "SAVE" else "EDIT PROFILE",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyberGreen,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .clickable {
                                    if (isEditingProfile) {
                                        val powerNum = editPower.toIntOrNull() ?: militaryPower
                                        tokenViewModel.updateProfile(
                                            name = editName,
                                            factionName = editFaction,
                                            tag = editTag,
                                            coords = editCoords,
                                            power = powerNum
                                        )
                                        isEditingProfile = false
                                        focusManager.clearFocus()
                                    } else {
                                        editName = playerName
                                        editFaction = faction
                                        editTag = allianceTag
                                        editCoords = coordinates
                                        editPower = militaryPower.toString()
                                        isEditingProfile = true
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (isEditingProfile) {
                        ProfileEditField(label = "Commander Name", value = editName, onValueChange = { editName = it })
                        ProfileEditField(label = "Alliance Tag", value = editTag, onValueChange = { editTag = it })
                        ProfileEditField(label = "Faction", value = editFaction, onValueChange = { editFaction = it })
                        ProfileEditField(label = "Base Coordinates", value = editCoords, onValueChange = { editCoords = it })
                        ProfileEditField(label = "Military Power Indicator", value = editPower, onValueChange = { editPower = it })
                    } else {
                        ProfileStaticField(label = "TACTICAL COGNOMEN", value = playerName)
                        ProfileStaticField(label = "ALLIANCE BADGE", value = "[$allianceTag]")
                        ProfileStaticField(label = "ALLEGIANCE FACTION", value = faction)
                        ProfileStaticField(label = "TARGET QUADRANT", value = coordinates)
                        ProfileStaticField(label = "TOTAL LEGIONS FORCE", value = String.format("%,.0f POWER", militaryPower.toFloat()))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileEditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label.uppercase(),
            fontFamily = FontFamily.Monospace,
            color = CyberGreen,
            fontSize = 9.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = LocalTextStyle.current.copy(
                color = BrightText,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderGreen.copy(alpha = 0.5f), RoundedCornerShape(4.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MilitaryBlack,
                unfocusedContainerColor = MilitaryBlack,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = CyberGreen
            ),
            singleLine = true
        )
    }
}

@Composable
fun ProfileStaticField(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            color = MutedText,
            fontSize = 11.sp
        )
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            color = BrightText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
