package unicsul.itinerario.tempoamigo;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import unicsul.itinerario.tempoamigo.action.AcaoAlertaRegistry;
import unicsul.itinerario.tempoamigo.databinding.ActivityMainBinding;
import unicsul.itinerario.tempoamigo.location.PermissaoHelper;
import unicsul.itinerario.tempoamigo.worker.ClimaWorker;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PermissaoHelper permissao;
    private NavController navController;
    private List<ImageButton> botoesMenu;

    private static final float LIFT_Y = -20f;
    private static final long ANIM_DURATION = 200L;

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
        navController = navHostFragment.getNavController();

        botoesMenu = Arrays.asList(
                binding.menuHome,
                binding.menuEdit,
                binding.menuSobre
        );

        NavOptions navOptions = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .build();

        binding.menuHome.setOnClickListener(v -> {
            navController.navigate(R.id.menu_home, null, navOptions);
            updateSelectedButton(R.id.menu_home);
        });

        binding.menuEdit.setOnClickListener(v -> {
            navController.navigate(R.id.menu_edit, null, navOptions);
            updateSelectedButton(R.id.menu_edit);
        });

        binding.menuSobre.setOnClickListener(v -> {
            navController.navigate(R.id.menu_sobre, null, navOptions);
            updateSelectedButton(R.id.menu_sobre);
        });

        binding.menuHome.setTranslationY(LIFT_Y);
    }

    private void updateSelectedButton(int selectedId) {
        for (ImageButton botao : botoesMenu) {
            botao.animate().cancel();

            float targetY = botao.getId() == selectedId ? LIFT_Y : 0f;
            botao.animate()
                    .translationY(targetY)
                    .setDuration(ANIM_DURATION)
                    .start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (botoesMenu != null) {
            for (ImageButton botao : botoesMenu) {
                botao.animate().cancel();
            }
        }
    }

    private void agendarWorker() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data inputData = new Data.Builder()
                .putString(ClimaWorker.INPUT_ACAO, AcaoAlertaRegistry.NOTIFICAR)
                .build();

        PeriodicWorkRequest trabalho = new PeriodicWorkRequest.Builder(
                ClimaWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                ClimaWorker.TAG,
                ExistingPeriodicWorkPolicy.UPDATE,
                trabalho
        );
    }
}