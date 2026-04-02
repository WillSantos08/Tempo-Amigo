package unicsul.itinerario.tempoamigo.location;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PermissaoHelper {

    public static final String PERMISSAO = Manifest.permission.ACCESS_FINE_LOCATION;

    private final AppCompatActivity activity;
    private final ActivityResultLauncher<String> launcher;
    private Runnable onConcedida;

    public PermissaoHelper(AppCompatActivity activity) {
        this.activity = activity;
        this.launcher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                concedida -> {
                    if (concedida && onConcedida != null) {
                        onConcedida.run();
                    }
                }
        );
    }

    public void solicitar(Runnable onConcedida) {
        this.onConcedida = onConcedida;

        if (temPermissao()) {
            onConcedida.run();
        } else {
            launcher.launch(PERMISSAO);
        }
    }

    public boolean temPermissao() {
        return ContextCompat.checkSelfPermission(activity, PERMISSAO)
                == PackageManager.PERMISSION_GRANTED;
    }
}