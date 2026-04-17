package unicsul.itinerario.tempoamigo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import unicsul.itinerario.tempoamigo.databinding.ActivityMainBinding;
import unicsul.itinerario.tempoamigo.location.PermissaoHelper;
import unicsul.itinerario.tempoamigo.worker.ClimaWorker;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PermissaoHelper permissao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initNavegacao();
        permissao = new PermissaoHelper(this);
        permissao.solicitar(this::agendarWorker);
    }

    private void initNavegacao() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    private void agendarWorker() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest trabalho = new PeriodicWorkRequest.Builder(
                ClimaWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                ClimaWorker.TAG,
                ExistingPeriodicWorkPolicy.UPDATE,
                trabalho
        );
    }
}