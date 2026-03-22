package dev.soncastro.lockingstrategiesdemo.usecase;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.soncastro.lockingstrategiesdemo.persistence.repository.AccountJpaRepository;
import dev.soncastro.lockingstrategiesdemo.persistence.repository.AccountMovementJpaRepository;
import dev.soncastro.lockingstrategiesdemo.persistence.repository.AfericaoJpaRepository;
import dev.soncastro.lockingstrategiesdemo.persistence.repository.PneuJpaRepository;
import jakarta.persistence.OptimisticLockException;

@Service
public class ExamplesLockingStrategiesUseCase {

    private static final Logger log = LoggerFactory.getLogger(ExamplesLockingStrategiesUseCase.class);

    @Autowired
    private AccountJpaRepository accountJpaRepository;

    @Autowired
    private AccountMovementJpaRepository accountMovementJpaRepository;

    @Autowired
    private PneuJpaRepository pneuJpaRepository;

    @Autowired
    private AfericaoJpaRepository afericaoJpaRepository;

    @Transactional
    public void reset() {
        final long accountId = 1L;
        log.debug("Iniciando reset para accountId={}", accountId);
        this.afericaoJpaRepository.deleteAll();
        this.afericaoJpaRepository.insertAfericao(1L, 2);
        this.accountMovementJpaRepository.deleteByAccountId(accountId);
        this.accountJpaRepository.resetBalance(1L, new BigDecimal("100"));
        log.debug("Reset concluido para accountId={}", accountId);
    }

    @Transactional
    public void debit(long accountId, BigDecimal amount) {

        log.debug("Iniciando debit para accountId={} amount={}", accountId, amount);

        BigDecimal currentBalance = this.accountJpaRepository.findBalanceById(accountId).orElseThrow();

        if (currentBalance.compareTo(amount) < 0) {
            log.debug("Saldo insuficiente");
            log.debug("Fim do escopo por saldo insuficiente");
            throw new RuntimeException("Saldo insuficiente");
        }

        try {
            log.debug("Aguardando 1 segundo para simular operação demorada");
            Thread.sleep(1000);
            log.debug("Fim do sleep");
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        BigDecimal newBalance = currentBalance.subtract(amount);

        this.accountJpaRepository.updateBalanceById(accountId, newBalance);
        this.accountMovementJpaRepository.insertAccountMovement(accountId, "DEBIT", amount);
        log.debug("Debit concluido para accountId={} newBalance={}", accountId, newBalance);
    }

    @Transactional
    public void debitPessimisticLocking(long accountId, BigDecimal amount) {

        log.debug("Iniciando debit para accountId={} amount={}", accountId, amount);

        this.accountJpaRepository.findForUpdate(accountId);

        BigDecimal currentBalance = this.accountJpaRepository.findBalanceById(accountId).orElseThrow();

        if (currentBalance.compareTo(amount) < 0) {
            log.debug("Saldo insuficiente");
            log.debug("Fim do escopo por saldo insuficiente");
            throw new RuntimeException("Saldo insuficiente");
        }

        try {
            log.debug("Aguardando 1 segundo para simular operação demorada");
            Thread.sleep(1000);
            log.debug("Fim do sleep");
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        BigDecimal newBalance = currentBalance.subtract(amount);

        this.accountJpaRepository.updateBalanceById(accountId, newBalance);
        this.accountMovementJpaRepository.insertAccountMovement(accountId, "DEBIT", amount);
        log.debug("Debit concluido para accountId={} newBalance={}", accountId, newBalance);
    }

    @Transactional
    public void debitOptimisticLocking(long accountId, BigDecimal amount) {

        log.debug("Iniciando debit para accountId={} amount={}", accountId, amount);

        Long version = this.accountJpaRepository.findVersionById(accountId).orElseThrow();

        BigDecimal currentBalance = this.accountJpaRepository.findBalanceById(accountId).orElseThrow();

        if (currentBalance.compareTo(amount) < 0) {
            log.debug("Saldo insuficiente");
            log.debug("Fim do escopo por saldo insuficiente");
            throw new RuntimeException("Saldo insuficiente");
        }

        try {
            log.debug("Aguardando 1 segundo para simular operação demorada");
            Thread.sleep(1000);
            log.debug("Fim do sleep");
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        BigDecimal newBalance = currentBalance.subtract(amount);

        if (this.accountJpaRepository.updateBalanceByIdAndVersion(accountId, newBalance, version) == 0) {
            log.debug("version nao encontrado");
            throw new OptimisticLockException("Version nao encontrado");
        }
        this.accountMovementJpaRepository.insertAccountMovement(accountId, "DEBIT", amount);
        log.debug("Debit concluido para accountId={} newBalance={}", accountId, newBalance);
    }

    @Transactional
    public void cenarioFicticioAfericaoPneuComInconsistencia(long pneuId, int vidaAfericao) {

        Integer ultimaVida = this.afericaoJpaRepository.findLastVidaByPneuId(pneuId);

        if (ultimaVida != null && vidaAfericao < ultimaVida) {
            throw new RuntimeException("Vida da afericao menor que a ultima vida");
        }
    
        // Força demora para vidaAfericao 2 para simular cenário de erro
        // test_pseudo-cenario-afericao-com-inconsistencia.js considera este cenário
        if (vidaAfericao == 2) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        this.afericaoJpaRepository.insertAfericao(pneuId, vidaAfericao);
    }

    @Transactional
    public void cenarioFicticioAfericaoPneuSemInconsistencia(Long pneuId, int vidaAfericao) {

        // Faz o lock na tabela pneu para garantir que não haja inconsistência
        // Quando usei este lock usei com FOR UPDATE NOWAIT, ou seja, se existe já um lock falha
        // Evitei repetir aqui para não aumentar a complexidade
        this.pneuJpaRepository.findByIdForUpdate(pneuId);

        Integer ultimaVida = this.afericaoJpaRepository.findLastVidaByPneuId(pneuId);

        if (ultimaVida != null && vidaAfericao < ultimaVida) {
            throw new RuntimeException("Vida da afericao menor que a ultima vida");
        }
    
        // Força demora para vidaAfericao 2 para simular cenário de erro
        // test_pseudo-cenario-afericao-com-inconsistencia.js considera este cenário
        // Na prática não faz diferença porque existe um lock acima
        // Permaneci com este código apenas para manter o padrão do outro método pseudoCenarioAfericaoPneuComInconsistencia
        if (vidaAfericao == 2) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        this.afericaoJpaRepository.insertAfericao(pneuId, vidaAfericao);
    }

}
