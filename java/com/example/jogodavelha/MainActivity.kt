package com.example.jogodavelha

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jogodavelha.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //Vetor bidimensional que representará o tabuleiro de jogo
    val tabuleiro = arrayOf(
        arrayOf("", "", ""),
        arrayOf("", "", ""),
        arrayOf("", "", "")
    )

    //Qual o Jogador está jogando
    var jogadorAtual = "X"
    var dificuldade = "facil" // Inicialmente, a dificuldade é fácil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura listeners para os botões
        configureButtonListeners()

        // Adiciona listener para mudar a dificuldade
        binding.switchDificuldade.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                dificuldade = "dificil"
                Toast.makeText(this, "Dificuldade: Difícil", Toast.LENGTH_SHORT).show()
            } else {
                dificuldade = "facil"
                Toast.makeText(this, "Dificuldade: Fácil", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Configura os botões
    private fun configureButtonListeners() {
        val buttons = arrayOf(
            binding.buttonZero, binding.buttonUm, binding.buttonDois,
            binding.buttonTres, binding.buttonQuatro, binding.buttonCinco,
            binding.buttonSeis, binding.buttonSete, binding.buttonOito
        )

        for (button in buttons) {
            button.setOnClickListener {
                buttonClick(it)
            }
        }
    }

    fun buttonClick(view: View) {
        //O botão clicado é associado com uma constante
        val buttonSelecionado = view as Button

        //De acordo com o botão clicado, a posição da matriz receberá o jogador
        when (buttonSelecionado.id) {
            binding.buttonZero.id -> tabuleiro[0][0] = jogadorAtual
            binding.buttonUm.id -> tabuleiro[0][1] = jogadorAtual
            binding.buttonDois.id -> tabuleiro[0][2] = jogadorAtual
            binding.buttonTres.id -> tabuleiro[1][0] = jogadorAtual
            binding.buttonQuatro.id -> tabuleiro[1][1] = jogadorAtual
            binding.buttonCinco.id -> tabuleiro[1][2] = jogadorAtual
            binding.buttonSeis.id -> tabuleiro[2][0] = jogadorAtual
            binding.buttonSete.id -> tabuleiro[2][1] = jogadorAtual
            binding.buttonOito.id -> tabuleiro[2][2] = jogadorAtual
        }

        buttonSelecionado.setBackgroundResource(R.drawable.pessoa)
        buttonSelecionado.isEnabled = false

        //Recebe o jogador vencedor através da função verificaTabuleiro
        var vencedor = verificaVencedor(tabuleiro)

        //Verifica se há um vencedor
        if (!vencedor.isNullOrBlank()) {
            Toast.makeText(this, "Vencedor: $vencedor", Toast.LENGTH_LONG).show()
            resetGame()
            return
        }

        //Se a dificuldade for "difícil", a melhor jogada para o jogador "O" é calculada
        if (dificuldade == "dificil") {
            val bestMove = getBestMove(tabuleiro)
            if (bestMove != null) {
                tabuleiro[bestMove.first][bestMove.second] = "O"
                updateButton(bestMove.first, bestMove.second, Color.RED)
            }
        } else {
            //Se a dificuldade for "fácil", uma jogada aleatória para o jogador "O" é gerada
            var rX: Int
            var rY: Int
            do {
                rX = Random.nextInt(0, 3)
                rY = Random.nextInt(0, 3)
            } while (tabuleiro[rX][rY] == "X" || tabuleiro[rX][rY] == "O")
            tabuleiro[rX][rY] = "O"
            updateButton(rX, rY, Color.RED)
        }

        vencedor = verificaVencedor(tabuleiro)

        if (!vencedor.isNullOrBlank()) {
            Toast.makeText(this, "Vencedor: $vencedor", Toast.LENGTH_LONG).show()
            resetGame()
        }
    }

    private fun resetGame() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    //Calcula a posição em um array com base nas coordenadas x e y
    private fun updateButton(x: Int, y: Int, color: Int) {
        val posicao = x * 3 + y
        when (posicao) {
            0 -> {
                binding.buttonZero.setBackgroundResource(R.drawable.robo)
                binding.buttonZero.isEnabled = false
            }
            1 -> {
                binding.buttonUm.setBackgroundResource(R.drawable.robo)
                binding.buttonUm.isEnabled = false
            }
            2 -> {
                binding.buttonDois.setBackgroundResource(R.drawable.robo)
                binding.buttonDois.isEnabled = false
            }
            3 -> {
                binding.buttonTres.setBackgroundResource(R.drawable.robo)
                binding.buttonTres.isEnabled = false
            }
            4 -> {
                binding.buttonQuatro.setBackgroundResource(R.drawable.robo)
                binding.buttonQuatro.isEnabled = false
            }
            5 -> {
                binding.buttonCinco.setBackgroundResource(R.drawable.robo)
                binding.buttonCinco.isEnabled = false
            }
            6 -> {
                binding.buttonSeis.setBackgroundResource(R.drawable.robo)
                binding.buttonSeis.isEnabled = false
            }
            7 -> {
                binding.buttonSete.setBackgroundResource(R.drawable.robo)
                binding.buttonSete.isEnabled = false
            }
            8 -> {
                binding.buttonOito.setBackgroundResource(R.drawable.robo)
                binding.buttonOito.isEnabled = false
            }
        }
    }

    //Calcula o melhor movimento
    private fun getBestMove(tabuleiro: Array<Array<String>>): Pair<Int, Int>? {
        var bestScore = Int.MIN_VALUE
        var move: Pair<Int, Int>? = null

        for (i in 0..2) {
            for (j in 0..2) {
                if (tabuleiro[i][j].isEmpty()) {
                    tabuleiro[i][j] = "O"
                    val score = minimax(tabuleiro, 0, false)
                    tabuleiro[i][j] = ""
                    if (score > bestScore) {
                        bestScore = score
                        move = Pair(i, j)
                    }
                }
            }
        }
        return move
    }

    private fun minimax(tabuleiro: Array<Array<String>>, depth: Int, isMaximizing: Boolean): Int {
        val result = verificaVencedor(tabuleiro)
        if (result != null) {
            return when (result) {
                "X" -> -1
                "O" -> 1
                "Empate" -> 0
                else -> 0
            }
        }

        if (isMaximizing) {
            var bestScore = Int.MIN_VALUE
            for (i in 0..2) {
                for (j in 0..2) {
                    if (tabuleiro[i][j].isEmpty()) {
                        tabuleiro[i][j] = "O"
                        val score = minimax(tabuleiro, depth + 1, false)
                        tabuleiro[i][j] = ""
                        bestScore = maxOf(score, bestScore)
                    }
                }
            }
            return bestScore
        } else {
            var bestScore = Int.MAX_VALUE
            for (i in 0..2) {
                for (j in 0..2) {
                    if (tabuleiro[i][j].isEmpty()) {
                        tabuleiro[i][j] = "X"
                        val score = minimax(tabuleiro, depth + 1, true)
                        tabuleiro[i][j] = ""
                        bestScore = minOf(score, bestScore)
                    }
                }
            }
            return bestScore
        }
    }

    fun verificaVencedor(tabuleiro: Array<Array<String>>): String? {
       
        // Verifica linhas e colunas
        for (i in 0 until 3) {
            
            //Verifica se há três itens iguais na linha
            if (tabuleiro[i][0] == tabuleiro[i][1] && tabuleiro[i][1] == tabuleiro[i][2] && tabuleiro[i][0].isNotEmpty()) {
                return tabuleiro[i][0]
            }
            //Verifica se há três itens iguais na coluna
            if (tabuleiro[0][i] == tabuleiro[1][i] && tabuleiro[1][i] == tabuleiro[2][i] && tabuleiro[0][i].isNotEmpty()) {
                return tabuleiro[0][i]
            }
        }
        
        // Verifica diagonais
        if (tabuleiro[0][0] == tabuleiro[1][1] && tabuleiro[1][1] == tabuleiro[2][2] && tabuleiro[0][0].isNotEmpty()) {
            return tabuleiro[0][0]
        }
        if (tabuleiro[0][2] == tabuleiro[1][1] && tabuleiro[1][1] == tabuleiro[2][0] && tabuleiro[0][2].isNotEmpty()) {
            return tabuleiro[0][2]
        }
        
        // Verifica a quantidade de jogadores
        var empate = 0
        for (linha in tabuleiro) {
            for (valor in linha) {
                if (valor == "X" || valor == "O") {
                    empate++
                }
            }
        }
        
        //Verifica se há empate
        if (empate == 9) {
            return "Empate"
        }
        
        //Nenhum vencedor
        return null
    }
}
