package com.example.streamliner;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.streamliner.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_time,  R.id.navigation_dashboard,R.id.navigation_chat, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // 监听导航事件并手动更新 UI
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // 根据 destination.id 更新标题或其他 UI 元素
            if (destination.getId() == R.id.navigation_home) {
                // 设置自定义标题
                setTitle("Home");
            } else if (destination.getId()==R.id.navigation_time){
                setTitle("Time");
            }
            else if (destination.getId() == R.id.navigation_dashboard) {
                setTitle("Learn");
            } else if (destination.getId()==R.id.navigation_chat) {
                setTitle("Chat");

            } else if (destination.getId() == R.id.navigation_notifications) {
                setTitle("Me");
            }

            // 如果不是顶级目的地，显示返回按钮逻辑
            boolean isTopLevelDestination = appBarConfiguration.getTopLevelDestinations().contains(destination.getId());
//            if (!isTopLevelDestination) {
//                // 显示返回按钮 (可以自定义实现)
//                showCustomBackButton();
//            } else {
//                // 隐藏返回按钮
//                hideCustomBackButton();
//            }
        });
        NavigationUI.setupWithNavController(binding.navView, navController);
    }



}