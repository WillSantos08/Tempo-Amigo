package unicsul.itinerario.tempoamigo.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.concurrent.CompletableFuture;

import unicsul.itinerario.tempoamigo.R;
import unicsul.itinerario.tempoamigo.database.AppDatabase;
import unicsul.itinerario.tempoamigo.database.ContatoEmergenciaDao;
import unicsul.itinerario.tempoamigo.model.ContatoEmergencia;

public class EdicaoFragment extends Fragment {

    private EditText editNome, editNumero, editMensagem;
    private Button buttonSalvar;
    private ContatoEmergenciaDao dao;
    private ContatoEmergencia contatoAtual;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edicao, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dao = AppDatabase.getInstance(requireContext()).contatoEmergenciaDao();

        editNome = view.findViewById(R.id.editNome);
        editNumero = view.findViewById(R.id.editNumero);
        editMensagem = view.findViewById(R.id.editMensagem);
        buttonSalvar = view.findViewById(R.id.buttonSalvar);

        carregarDados();

        TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) { atualizarBotao(); }
        };

        editNome.addTextChangedListener(watcher);
        editNumero.addTextChangedListener(watcher);
        editMensagem.addTextChangedListener(watcher);

        buttonSalvar.setOnClickListener(v -> salvar());
    }

    private void carregarDados() {
        CompletableFuture.supplyAsync(() -> dao.buscarUltimo())
                .thenAcceptAsync(contato -> {
                    contatoAtual = contato;
                    if (contato != null) {
                        editNome.setText(contato.nome);
                        editNumero.setText(contato.numero);
                        editMensagem.setText(contato.mensagemInicial);
                    }
                }, requireActivity().getMainExecutor());
    }

    private void salvar() {
        String nome = editNome.getText().toString().trim();
        String numero = editNumero.getText().toString().trim();
        String mensagem = editMensagem.getText().toString().trim();

        CompletableFuture.runAsync(() -> {
            if (contatoAtual == null) {
                dao.inserir(new ContatoEmergencia(numero, nome, mensagem));
            } else {
                ContatoEmergencia atualizado = new ContatoEmergencia(numero, nome, mensagem);
                atualizado.id = contatoAtual.id;
                dao.atualizar(atualizado);
            }
        }).thenRunAsync(() -> {
            contatoAtual = new ContatoEmergencia(numero, nome, mensagem);
            atualizarBotao();
        }, requireActivity().getMainExecutor());
    }

    private void atualizarBotao() {
        String nome = editNome.getText().toString().trim();
        String numero = editNumero.getText().toString().trim();
        String mensagem = editMensagem.getText().toString().trim();

        boolean preenchido = !nome.isEmpty() && !numero.isEmpty() && !mensagem.isEmpty();
        boolean alterado = contatoAtual == null
                || !nome.equals(contatoAtual.nome)
                || !numero.equals(contatoAtual.numero)
                || !mensagem.equals(contatoAtual.mensagemInicial);

        buttonSalvar.setEnabled(preenchido && alterado);
    }
}