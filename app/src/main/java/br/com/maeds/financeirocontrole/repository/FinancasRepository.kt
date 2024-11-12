package br.com.maeds.financeirocontrole.repository

import android.content.ContentValues.TAG
import android.util.Log
import br.com.maeds.financeirocontrole.model.Financa
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class FinancasRepository {

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun salvar(tipo: String, valor: Double, data: String, categoria: String) {
        Log.d(TAG, "Iniciando cadastro registro")

        val identificadoUnicoFinanca = UUID.randomUUID().toString()
        val model = Financa(identificadoUnicoFinanca, tipo, valor, data, categoria)

        firestore.collection("financas")
            .document(identificadoUnicoFinanca)
            .set(model)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Sucesso ao incluir registro $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao inserir registro", e)
            }
    }

    fun listarTodos(): Flow<List<Financa>> = callbackFlow {
        firestore.collection("financas")
            .addSnapshotListener { query, erro ->
                if (erro != null) {
                    Log.e(TAG, "Erro ao inserir registro", erro)
                    close(erro)
                } else {
                    val financas = query?.documents?.mapNotNull { it.toObject(Financa::class.java) } ?: emptyList()
                    trySend(financas)
                }
            }
        awaitClose { }
    }

}