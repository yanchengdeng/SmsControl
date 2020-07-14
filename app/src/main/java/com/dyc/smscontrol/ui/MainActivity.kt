package com.dyc.smscontrol.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.NavHostFragment
import com.blankj.utilcode.util.ActivityUtils
import com.dyc.smscontrol.R
import com.dyc.smscontrol.ui.fragments.SmsFragment
import com.dyc.smscontrol.ui.fragments.MineFragment
import com.dyc.smscontrol.ui.nav.FixFragmentNavigator
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * author : yanc
 * data : 2020/7/9
 * time : 0:03
 * desc : 首页
 */
class MainActivity : AppCompatActivity() {
    private lateinit var navView: BottomNavigationView
    private lateinit var navController : NavController


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navView = findViewById(R.id.nav_view)

//        val navController = findNavController(R.id.nav_host_fragment)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_notifications
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)


        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navController = NavHostFragment.findNavController(fragment!!)
        //创建自定义导航过滤器
        val fragmentNavigator = FixFragmentNavigator(this,fragment.childFragmentManager,fragment.id)
        //获取导航提供者
        val provider = navController.navigatorProvider!!
        //添加自定义frament导航器进去
        provider.addNavigator(fragmentNavigator)
        //手动创建导航图
        val navGraph = initNavGraph(provider,fragmentNavigator)
        //设置导航图
        navController.graph = navGraph
        //底部导航点击事件
        //底部导航设置点击事件
        navView.setOnNavigationItemSelectedListener { item: MenuItem ->
            navController.navigate(item.itemId)
            true
        }


    }

    private fun initNavGraph(
        provider: NavigatorProvider,
        fragmentNavigator: FixFragmentNavigator
    ): NavGraph {
        val navGraph = NavGraph(NavGraphNavigator(provider))
        //用自定义的导航器来创建目的地


        val mainDestination = fragmentNavigator.createDestination()
        mainDestination.id = R.id.navigation_home
        mainDestination.className = SmsFragment::class.java.canonicalName!!
        mainDestination.label = getString(R.string.title_sms)
        navGraph.addDestination(mainDestination)

        val mineDestination = fragmentNavigator.createDestination()
        mineDestination.id = R.id.navigation_mine
        mineDestination.className = MineFragment::class.java.canonicalName!!
        mineDestination.label = getString(R.string.title_mine)
        navGraph.addDestination(mineDestination)

        navGraph.startDestination = R.id.navigation_home
        return navGraph

    }
//    override fun onBackPressed() {
//        val currentId: Int = navController.currentDestination?.id!!
//        val startId: Int = navController.graph.startDestination
//        //如果当前目的地不是HomeFragment，则先回到HomeFragment
//        if (currentId != startId) {
//            navView.selectedItemId = startId
//        } else {
//            finish()
//        }
//    }


    private var firstClickTime: Long = 0

    override fun onBackPressed() {
            if (System.currentTimeMillis() - firstClickTime < 2000) {
                ActivityUtils.finishAllActivities()
            } else {
                Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show()
                firstClickTime = System.currentTimeMillis()
            }
        }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_HOME){
            moveTaskToBack(false)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


}