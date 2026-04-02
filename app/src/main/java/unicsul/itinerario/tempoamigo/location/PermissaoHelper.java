package unicsul.itinerario.tempoamigo.location;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PermissaoHelper {

    private static final String[] PERMISSOES = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
    };

    private final AppCompatActivity activity;
    private final ActivityResultLauncher<String[]> launcher;
    private Runnable onConcedida;

    public PermissaoHelper(AppCompatActivity activity) {
        this.activity = activity;
        this.launcher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                resultado -> {
                    boolean todasConcedidas = resultado.values()
                            .stream()
                            .allMatch(Boolean::booleanValue);

                    if (todasConcedidas && onConcedida != null) {
                        onConcedida.run();
                    }
                }
        );
    }

    public void solicitar(Runnable onConcedida) {
        this.onConcedida = onConcedida;

        if (temTodasPermissoes()) {
            onConcedida.run();
        } else {
            launcher.launch(PERMISSOES);
        }
    }

    public boolean temTodasPermissoes() {
        for (String permissao : PERMISSOES) {
            if (ContextCompat.checkSelfPermission(activity, permissao)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}