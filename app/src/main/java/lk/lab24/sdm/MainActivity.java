package lk.lab24.sdm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import lk.lab24.sdm.Backend.Actions;
import lk.lab24.sdm.dialogs.newDownlod;
import lk.lab24.sdm.dialogs.resumeDownlod;


public class MainActivity extends AppCompatActivity {
	private static final String TAG = "fuck";
	public static Context context;
	Intent i;
	FloatingActionButton fab, add, resume;
	Database db;
	Intent downlodServiceIntent;
	//Dialog Fragment
	private newDownlod dialogFragmentNew;
	private resumeDownlod dialogFragmentResume;

	@Override

	protected void onCreate(Bundle savedInstanceState) {
		System.setProperty("http.keepAlive", "false");
		super.onCreate(savedInstanceState);
		//database
		this.context = getApplicationContext();
		this.db = new Database(this);
//        CDmMain cDmMain = new CDmMain(this.db,this);
		setContentView(R.layout.activity_main);
		downlodServiceIntent = new Intent(this, MyIntentService.class);
		startService(downlodServiceIntent);
		Log.d("fuck", "onCreate: ");


// viewpagger with tab
		tablayoutwithViewPager();
//navigation Drawer
		navigationDrawer();
//fab
		fab();
		upgradeSecurityProvider();


	}

	private void upgradeSecurityProvider() {
		ProviderInstaller.installIfNeededAsync(this, new ProviderInstaller.ProviderInstallListener() {
			@Override
			public void onProviderInstalled() {

			}

			@Override
			public void onProviderInstallFailed(int errorCode, Intent recoveryIntent) {
//        GooglePlayServicesUtil.showErrorNotification(errorCode, MainApplication.this);
				GoogleApiAvailability.getInstance().showErrorNotification(MainActivity.this, errorCode);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {


		if (item.getItemId() == R.id.startall) {
			Log.d(TAG, "onOptionsItemSelected: StartAll");
			LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Actions.ACTION_startAll));
		} else if (item.getItemId() == R.id.pauseall) {
			Log.d(TAG, "onOptionsItemSelected: Pause all");
			LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Actions.ACTION_pauseAll));
		}
		return super.onOptionsItemSelected(item);
	}

	protected void tablayoutwithViewPager() {
		ViewPager2 viewPager = (ViewPager2) findViewById(R.id.viewPagger);
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		viewPager.setAdapter(new FragmentPageAdapter(this));
		new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
			@Override
			public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
				switch (position) {
					case 0:
						tab.setText("All");
						break;
					case 1:
						tab.setText("Downloding");
						break;
					case 2:
						tab.setText("Completed");
						break;
					case 3:
						tab.setText("Interrupted");
						break;
				}
			}
		}).attach();


	}


	//For Floating Action Button
	protected void fab() {
		this.resume = findViewById(R.id.resume);
		this.fab = findViewById(R.id.fab);
		this.add = findViewById(R.id.add);
		this.resume.setOnClickListener(v -> {
//            dialogFragmentResume = new resumeDownlod();
//            dialogFragmentResume.show(getSupportFragmentManager(), "resumeDownlod");


		});
		this.add.setOnClickListener(v -> {
			//  startService(downlodServiceIntent);
			dialogFragmentNew = new newDownlod(MainActivity.this.db);
			dialogFragmentNew.show(getSupportFragmentManager(), "newDownlod");
		});


		new Fab(this, this.fab, this.resume, this.add);
	}


	//for navigation drawer
	protected void navigationDrawer() {
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		NavigationView navigationView = findViewById(R.id.nav_view);
		//set navigation toogle
		ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(actionBarDrawerToggle);
		actionBarDrawerToggle.syncState();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 200) {
			Log.d("fuck", "onActivityResult: " + resultCode);
			String url = data.getStringExtra("URL");
			Log.d("fuck", "onActivityResult: " + resultCode + " URL :" + url);
			dialogFragmentNew.setValues(url);
		}
	}
}
