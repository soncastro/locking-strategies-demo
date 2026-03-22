package dev.soncastro.lockingstrategiesdemo.controller;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.soncastro.lockingstrategiesdemo.persistence.entity.AccountMovementEntity;
import dev.soncastro.lockingstrategiesdemo.usecase.ExamplesLockingStrategiesUseCase;

@RestController
@RequestMapping("/locking-strategies-demo")
public class ExamplesController {

    private final static Logger log = LoggerFactory.getLogger(ExamplesController.class);

    private final ExamplesLockingStrategiesUseCase examplesLockingStrategiesUseCase;

    public ExamplesController(ExamplesLockingStrategiesUseCase examplesLockingStrategiesUseCase) {
        this.examplesLockingStrategiesUseCase = examplesLockingStrategiesUseCase;
    }

    @PostMapping("/reset-and-update-balance/{accountId}/{newBalance}")
    public int resetAndUpdateBalance(@PathVariable long accountId, @PathVariable BigDecimal newBalance) {
        log.debug("Recebida requisicao resetAndUpdateBalance para accountId={} newBalance={}", accountId, newBalance);
        int val = this.examplesLockingStrategiesUseCase.resetAndUpdateBalance(accountId, newBalance);
        log.debug("Fim do escopo da requisicao resetAndUpdateBalance para accountId={} newBalance={}", accountId, newBalance);
        return val;
    }   

    @GetMapping("/balance/{accountId}")
    public BigDecimal getBalance(@PathVariable long accountId) {
        log.debug("Recebida requisicao getBalance para accountId={}", accountId);
        BigDecimal val = this.examplesLockingStrategiesUseCase.getBalance(accountId);
        log.debug("Fim do escopo da requisicao getBalance para accountId={}", accountId);
        return val;
    }

    @GetMapping("/movements/{accountId}")
    public List<AccountMovementEntity> getMovements(@PathVariable long accountId) {
        log.debug("Recebida requisicao getMovements para accountId={}", accountId);
        List<AccountMovementEntity> val = this.examplesLockingStrategiesUseCase.getMovements(accountId);
        log.debug("Fim do escopo da requisicao getMovements para accountId={}", accountId);
        return val;
    }

    @PostMapping("/debit/{accountId}/{amount}")
    public void debit(@PathVariable long accountId, @PathVariable BigDecimal amount) {
        log.debug("Recebida requisicao debit para accountId={} amount={}", accountId, amount);
        this.examplesLockingStrategiesUseCase.debit(accountId, amount);
        log.debug("Fim do escopo da requisicao debit para accountId={} amount={}", accountId, amount);
    }

    @PostMapping("/debit-synchronized/{accountId}/{amount}")
    public void debitSynchronized(@PathVariable long accountId, @PathVariable BigDecimal amount) {
        log.debug("Recebida requisicao debitSynchronized para accountId={} amount={}", accountId, amount);
        synchronized (ExamplesController.class) {
            this.examplesLockingStrategiesUseCase.debit(accountId, amount);
        }
        log.debug("Fim do escopo da requisicao debitSynchronized para accountId={} amount={}", accountId, amount);
    }

    @PostMapping("/debit-pessimistic-locking/{accountId}/{amount}")
    public void debitPessimisticLocking(@PathVariable long accountId, @PathVariable BigDecimal amount) {
        log.debug("Recebida requisicao debitPessimisticLocking para accountId={} amount={}", accountId, amount);
        this.examplesLockingStrategiesUseCase.debitPessimisticLocking(accountId, amount);
        log.debug("Fim do escopo da requisicao debitPessimisticLocking para accountId={} amount={}", accountId, amount);
    }

    @PostMapping("/debit-optimistic-locking/{accountId}/{amount}")
    public void debitOptimisticLocking(@PathVariable long accountId, @PathVariable BigDecimal amount) {
        log.debug("Recebida requisicao debitOptimisticLocking para accountId={} amount={}", accountId, amount);
        this.examplesLockingStrategiesUseCase.debitOptimisticLocking(accountId, amount);
        log.debug("Fim do escopo da requisicao debitOptimisticLocking para accountId={} amount={}", accountId, amount);
    }

    @PostMapping("/pseudo-cenario-afericao-pneu-com-inconsistencia/{pneuId}/{vidaAfericao}")
    public void pseudoCenarioAfericaoPneuComInconsistencia(@PathVariable long pneuId, @PathVariable int vidaAfericao) {
        log.debug("Recebida requisicao pseudoCenarioAfericaoPneuComInconsistencia para pneuId={} vidaAfericao={}", pneuId, vidaAfericao);
        this.examplesLockingStrategiesUseCase.pseudoCenarioAfericaoPneuComInconsistencia(pneuId, vidaAfericao);
        log.debug("Fim do escopo da requisicao pseudoCenarioAfericaoPneuComInconsistencia para pneuId={} vidaAfericao={}", pneuId, vidaAfericao);
    }

    @PostMapping("/pseudo-cenario-afericao-pneu-sem-inconsistencia/{pneuId}/{vidaAfericao}")
    public void pseudoCenarioAfericaoPneuSemInconsistencia(@PathVariable long pneuId, @PathVariable int vidaAfericao) {
        log.debug("Recebida requisicao pseudoCenarioAfericaoPneuSemInconsistencia para pneuId={} vidaAfericao={}", pneuId, vidaAfericao);
        this.examplesLockingStrategiesUseCase.pseudoCenarioAfericaoPneuSemInconsistencia(pneuId, vidaAfericao);
        log.debug("Fim do escopo da requisicao pseudoCenarioAfericaoPneuSemInconsistencia para pneuId={} vidaAfericao={}", pneuId, vidaAfericao);
    }    

}
