package com.appa.snoop.presentation.ui.category

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.appa.snoop.domain.model.NetworkResult
import com.appa.snoop.presentation.R
import com.appa.snoop.presentation.common.button.ClickableButton
import com.appa.snoop.presentation.common.topbar.component.CategoryTopbar
import com.appa.snoop.presentation.navigation.NavUtil
import com.appa.snoop.presentation.navigation.Router
import com.appa.snoop.presentation.ui.category.component.BottomSheetItem
import com.appa.snoop.presentation.ui.category.component.CategoryItem
import com.appa.snoop.presentation.ui.category.component.PriceRangeView
import com.appa.snoop.presentation.ui.category.component.SnoopSearchBar
import com.appa.snoop.presentation.ui.theme.DarkGrayColor
import com.appa.snoop.presentation.ui.theme.PrimaryColor
import com.appa.snoop.presentation.ui.theme.WhiteColor
import com.appa.snoop.presentation.util.extensions.addFocusCleaner
import com.kakao.sdk.friend.m.s
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.internal.wait

private const val TAG = "[김희웅] CategoryScreen"
const val SIZE = 2
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    showSnackBar: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val scrollableState = rememberScrollableState{ 1f }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val pagingData = categoryViewModel.pagingDataFlow.collectAsLazyPagingItems()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val lazyState = rememberLazyGridState()

    val snackState = remember { SnackbarHostState() }

    val wishToggleState = categoryViewModel.wishToggleState.collectAsState()

//    scope.launch {
//        categoryViewModel.wishToggleState.collectLatest {
//            snackState.showSnackbar(
//                message = "위시리스트에 추가되었습니다.",
//                actionLabel = "확인하러 가기 ->"
//            )
//        }
//    }

    LaunchedEffect(key1 = Unit) {
        CategoryTopbar.buttons
            .onEach { button ->
                when (button) {
                    CategoryTopbar.AppBarIcons.ChatIcon -> {
                        navController.navigate(Router.CATEGORY_CHATTING_ROUTER_NAME)
                    }
                    CategoryTopbar.AppBarIcons.MenuIcon -> {
                        if (drawerState.isOpen)
                            drawerState.close()
                        else
                            drawerState.open()
                    }
                }
            }.launchIn(this)
    }

    // TODO 검색에 성공해도 스낵바가 뜨는 버그가 있음
    LaunchedEffect(pagingData.itemCount, categoryViewModel.keywordSearchState) {
        if (pagingData.itemCount == 0 && categoryViewModel.keywordSearchState > 0) {
            showSnackBar("검색 결과가 없습니다.")
        }
    }

    LaunchedEffect(drawerState.isOpen) {
        if (!drawerState.isOpen) {
            focusManager.clearFocus()
        }
    }

    // TODO 끝까지 내리면 다시 처음으로 돌아가는 버그가 있음
    LaunchedEffect(pagingData.itemCount) {
//        lazyState.scrollToItem(0)
//        showSnackBar("끝")
    }

//    LaunchedEffect(wishToggleState.value) {
//        if (wishToggleState.value > 0) {
//            val job = scope.launch {
////                when(
//                snackState.showSnackbar(
//                    message = "위시리스트에 담겼습니다.",
//                    duration = SnackbarDuration.Indefinite,
//                    actionLabel = "찜 목록 보러가기 ->"
//                )
////                )) {
////                    SnackbarResult.ActionPerformed -> {
////                        navController.navigate(Router.MAIN_LIKE_ITEM_ROUTER_NAME)
////                    }
////                    else -> {
////
////                    }
////                }
//            }
//            delay(1500L)
//            job.cancel()
//        }
//    }

    ModalNavigationDrawer(
        modifier = Modifier
            .addFocusCleaner(focusManager),
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(260.sdp),
                drawerTonalElevation = 0.sdp
            ) {
                Column(
                    modifier = Modifier
                        .background(WhiteColor)
                        .fillMaxSize()
                ) {
                    SnoopSearchBar(
                        modifier = Modifier
                            .wrapContentHeight(),
                        focusManager = focusManager,
                        categoryViewModel = categoryViewModel,
                        showSnackBar = showSnackBar,
                        onSearching = {
                            scope.launch {
                                focusManager.clearFocus()
                                categoryViewModel.getProductListByKeywordPaging(categoryViewModel.textSearchState)
                                categoryViewModel.keywordSearchClick()
                                drawerState.close()
                            }
                        }
                    )
                    ClickableButton(
                        onClick = {
                            focusManager.clearFocus()
                            categoryViewModel.priceRangeStateToggle()
                        },
                        modifier = Modifier
                            .padding(start = 16.sdp, top = 16.sdp),
                        buttonColor = if (categoryViewModel.priceRangeState) DarkGrayColor else PrimaryColor
                    ) {
                        Row {
                            Text(
                                text = "가격 범위 정하기",
                                fontSize = 10.ssp
                            )
                        }
                    }
                    AnimatedContent(
                        targetState = categoryViewModel.priceRangeState,
                        transitionSpec = {
                            scaleIn(
                                transformOrigin = TransformOrigin(0.5f, -1f)
                            ) togetherWith scaleOut(
                                transformOrigin = TransformOrigin(0.5f, -1f)
                            )
                        },
                        label = ""
                    ) { it ->
                        if (it) {
                            PriceRangeView(
                                modifier = Modifier
                                    .padding(start = 16.sdp, end = 16.sdp)
                                    .wrapContentHeight(),
                                focusManager = focusManager,
                                categoryViewModel = categoryViewModel
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.sdp)
                    ) {
                        for (it in CategoryList.list) {
                            BottomSheetItem(
                                majorName = it.majorName,
                                categoryViewModel = categoryViewModel,
                                categoryState = when(it.majorName) {
                                    "디지털가전" -> categoryViewModel.digitalCategoryState
                                    "가구" -> categoryViewModel.furnitureCategoryState
                                    "생활용품" -> categoryViewModel.necessariesCategoryState
                                    "식품" -> categoryViewModel.foodCategoryState
                                    else -> false
                                },
                                minorList = it.minorList,
                                onClick = {
                                    categoryViewModel.sheetSelect(it.majorName)
                                },
                                onDismiss = {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .addFocusCleaner(focusManager),
            snackbarHost = {
                SnackbarHost(snackState)
            }
        ) { paddingValue ->
            paddingValue
            Column(
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (pagingData.itemCount == 0) {
                    Text(
                        text = "기웃기웃의 둘러보기 기능입니다.",
                        fontSize = 18.ssp
                    )
                    Spacer(modifier = Modifier.height(12.sdp))
                    Text(
                        text = "카테고리 & 검색 기능을 이용해주세요.",
                        fontSize = 14.ssp
                    )
                    Spacer(modifier = Modifier.height(12.sdp))
                    Image(
                        modifier = Modifier
                            .size(200.sdp),
                        painter = painterResource(id = R.drawable.img_meerkat_face),
                        contentDescription = null
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(SIZE),
                        state = lazyState
                    ) {
                        items(
                            pagingData.itemCount,
                        ) {
                            CategoryItem(
                                modifier = Modifier,
                                product = pagingData[it]!!,
                                onItemClicked = {
                                    navController.navigate(Router.CATEGORY_PRODUCT_ROUTER_NAME)
                                },
                                onLikeClicked = {
                                    // TODO 구현 찜 토글
                                    scope.launch {
                                        when (categoryViewModel.isLogined().accessToken) {
                                            "no_token_error" -> { // 미 로그인 시
                                                val job = scope.launch {
                                                    snackState.showSnackbar(
                                                        message = "로그인이 필요한 기능입니다.",
                                                        duration = SnackbarDuration.Indefinite
                                                    )
                                                }
                                                delay(1500L)
                                                job.cancel()
                                            }
                                            else -> { // 로그인 시
//                                                categoryViewModel.postWishToggle(pagingData[it]!!.code)
                                                when(categoryViewModel.toggled(pagingData[it]!!.code)) {
                                                    is NetworkResult.Success-> {
                                                            when(snackState.showSnackbar(
                                                                message = "위시리스트에 추가되었습니다.",
                                                                actionLabel = "확인하러 가기 ->"
                                                            )) {
                                                                // TODO 다시 바텀네비게이션 바 눌렀을 때 안돌아옴
                                                                // TODO 다시 돌아와도 하트가 눌려있지 않음
                                                                SnackbarResult.ActionPerformed -> {
                                                                    navController.navigate(Router.MAIN_LIKE_ITEM_ROUTER_NAME)
                                                                }
                                                                else -> {

                                                                }
                                                            }
                                                    }
                                                    else -> {

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

//    Scaffold(
//        modifier = Modifier
//            .fillMaxSize()
//            .addFocusCleaner(focusManager),
//        topBar = {
//            AnimatedContent(
//                targetState = categoryViewModel.searchBarState,
//                transitionSpec = {
//                     slideInVertically(
//                         initialOffsetY = { -200 },
//                         animationSpec = tween(200)
//                     ) togetherWith slideOutVertically (
//                         targetOffsetY = { -200 },
//                         animationSpec = tween(200)
//                     ) using SizeTransform(false)
//                },
//                label = ""
//            ) { searchBarVisible ->
//                if (searchBarVisible) {
//                    SnoopSearchBar(
//                        modifier = Modifier
//                            .wrapContentHeight(),
//                        focusManager = focusManager,
//                        categoryViewModel = categoryViewModel,
//                        showSnackBar = showSnackBar
//                    )
//                }
//            }
//        },
//    ) { paddingValue ->
//        paddingValue
//        Column(
//            modifier = modifier
//                .fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            if (pagingData.itemCount == 0) {
////                DrawerView()
//                Text(
//                    text = "기웃기웃의 둘러보기 기능입니다.",
//                    fontSize = 18.ssp
//                )
//                Spacer(modifier = Modifier.height(12.sdp))
//                Text(
//                    text = "카테고리 & 검색 기능을 이용해주세요.",
//                    fontSize = 14.ssp
//                )
//                Spacer(modifier = Modifier.height(12.sdp))
//                Image(
//                    modifier = Modifier
//                        .size(200.sdp),
//                    painter = painterResource(id = R.drawable.img_meerkat_face),
//                    contentDescription = null
//                )
//            } else {
//                LazyVerticalGrid(
//                    columns = GridCells.Fixed(SIZE),
//                ) {
//                    items(
//                        pagingData.itemCount,
//                        key = {
//                            pagingData[it]!!.id
//                        }
//                    ) {
//                        CategoryItem(
//                            modifier = Modifier,
//                            product = pagingData[it]!!,
//                            onItemClicked = {
//                                navController.navigate(Router.CATEGORY_PRODUCT_ROUTER_NAME)
//                            },
//                            onLikeClicked = {
//                                // TODO 구현 찜 토글
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    }
}