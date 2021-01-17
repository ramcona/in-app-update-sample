package can.co.id.in_appupdate

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class MainActivity : AppCompatActivity() {

    lateinit var appUpdateManager: AppUpdateManager
    private val MY_REQUEST_UPDATE = 124

    override fun onStart() {
        super.onStart()

        appUpdateManager = AppUpdateManagerFactory.create(this)
        periksaPembaruan()

        //kode untuk mendaftarkan listerner pada appUpdateManger
        appUpdateManager.registerListener(listenerPembaruan)
    }

    override fun onStop() {
        super.onStop()

        //kode untuk membatalkan pendaftaran listerner pada appUpdateManger
        appUpdateManager.unregisterListener(listenerPembaruan)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun periksaPembaruan() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                )) {
                Log.d("MainACtivity", "Pembaruan tersedia")

                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    MY_REQUEST_UPDATE)

            } else {
                Log.d("MainACtivity", "Tidak ada pembaruan tersedia")
            }
        }
    }

    private val listenerPembaruan: InstallStateUpdatedListener? = InstallStateUpdatedListener { installState ->
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            Log.d("MainACtivity", "pembaruaan aplikasi berhasil")

            android.widget.Toast.makeText(this, "Berhasil memperbarui aplikasi", android.widget.Toast.LENGTH_LONG).show()

            //metode untuk restart aplikasi setelah update
            appUpdateManager.completeUpdate()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // menerima hasil dari pembaruan aplikasi
        if (requestCode == MY_REQUEST_UPDATE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.d("MainActivity", "Pembaruan berhasil")
                }
                Activity.RESULT_CANCELED -> {
                    Log.d("MainActivity", "Pembaruan dibatalkan")
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    Log.d("MainActivity", "Pembaruan gagal")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {

                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    MY_REQUEST_UPDATE
                );
            }
        }
    }

}