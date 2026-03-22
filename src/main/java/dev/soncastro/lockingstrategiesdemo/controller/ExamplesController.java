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

    @PostMapping("/reset")
    public void reset() {
        log.debug("Recebida requisicao reset");
        this.examplesLockingStrategiesUseCase.reset();
        log.debug("Fim do escopo da requisicao reset");
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

    @PostMapping("/cenario-ficticio-afericao-pneu-com-inconsistencia/{pneuId}/{vidaAfericao}")
    public void cenarioFicticioAfericaoPneuComInconsistencia(@PathVariable long pneuId, @PathVariable int vidaAfericao) {
        log.debug("Recebida requisicao cenarioFicticioAfericaoPneuComInconsistencia para pneuId={} vidaAfericao={}", pneuId, vidaAfericao);
        this.examplesLockingStrategiesUseCase.cenarioFicticioAfericaoPneuComInconsistencia(pneuId, vidaAfericao);
        log.debug("Fim do escopo da requisicao cenarioFicticioAfericaoPneuComInconsistencia para pneuId={} vidaAfericao={}", pneuId, vidaAfericao);
    }

    @PostMapping("/cenario-ficticio-afericao-pneu-sem-inconsistencia/{pneuId}/{vidaAfericao}")
    public void cenarioFicticioAfericaoPneuSemInconsistencia(@PathVariable long pneuId, @PathVariable int vidaAfericao) {
        log.debug("Recebida requisicao cenarioFicticioAfericaoPneuSemInconsistencia para pneuId={} vidaAfericao={}", pneuId, vidaAfericao);
        this.examplesLockingStrategiesUseCase.cenarioFicticioAfericaoPneuSemInconsistencia(pneuId, vidaAfericao);
        log.debug("Fim do escopo da requisicao cenarioFicticioAfericaoPneuSemInconsistencia para pneuId={} vidaAfericao={}", pneuId, vidaAfericao);
    }    

}
