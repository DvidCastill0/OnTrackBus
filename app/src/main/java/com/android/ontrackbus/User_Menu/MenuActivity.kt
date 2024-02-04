package com.android.ontrackbus.User_Menu

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.ontrackbus.MainActivity
import com.android.ontrackbus.R
import com.android.ontrackbus.User_Menu.ui.help.HelpWindow
import com.android.ontrackbus.databinding.ActivityMenuBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class MenuActivity : AppCompatActivity() {

    //boton y variable para cerrar sesion
    private var mAuth: FirebaseAuth? = null
    var drawerLayout: DrawerLayout? = null


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var sharedViewModel = ViewModelProvider(this)[MenuViewModel::class.java]
        sharedViewModel.loginBundle = intent.extras;


        //se instancian las variables de autenticacion de firebase
        //se instancian las variables de autenticacion de firebase
        mAuth = FirebaseAuth.getInstance()

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMenu.toolbar)

        binding.appBarMenu.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_menu)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_channels, R.id.nav_maps, R.id.nav_trusted_contact, R.id.nav_daily_activity, R.id.nav_profile
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Navegar al Fragmento 1 por defecto
        //navController.navigate(R.id.nav_maps, intent.extras)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {

            android.R.id.home -> {
                // Manejar clic en el botón de navegación
                if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout!!.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout!!.openDrawer(GravityCompat.START)
                }
                return true
            }

            R.id.item_help ->
                // se finaliza la actividad para que no pueda volver hacia atras
                startActivity(Intent(this, HelpWindow::class.java))

            R.id.item_close_sesion -> {
                //se usa el metodo para cerrar sesion.
                mAuth!!.signOut()
                // se finaliza la actividad para que no pueda volver hacia atras
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        // Asegúrate de llamar a la implementación de la superclase
        return super.onOptionsItemSelected(menuItem);
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_menu)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}