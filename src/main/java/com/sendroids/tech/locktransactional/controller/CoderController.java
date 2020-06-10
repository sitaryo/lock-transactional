package com.sendroids.tech.locktransactional.controller;

import com.sendroids.tech.locktransactional.service.CoderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RequestMapping("/coder")
@RestController
@Slf4j
public class CoderController {
    private final CoderService coderService;

    public CoderController(
            CoderService coderService
    ) {
        this.coderService = coderService;
    }

    @GetMapping("/raise")
    @Transactional
    public void raiseSalary() {
        log.info("raise");
        coderService.raiseSalaryNoTransactional(1L, 1000D);
    }

    @GetMapping("/exceptionRollBack")
    @Transactional
    public void exceptionRollback() {
        coderService.raiseSalaryTransactional(1L, 1000D);
        // salary == 0  throw CoderSalaryException
        coderService.payCutTransactional(2L, 1000D);
        // user not found
//        coderService.raiseSalaryNoTransactional(999L, 1000D);
    }

    @GetMapping("/deathLock/{firstId}/{secondId}")
    @Transactional
    public void deathLock(
            @PathVariable Long firstId,
            @PathVariable Long secondId
    ) {
        coderService.raiseSalaryNoTransactional(firstId, 1000D);
        try {
            TimeUnit.SECONDS.sleep(3L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        coderService.raiseSalaryNoTransactional(secondId, 1000D);
    }


    @GetMapping("/versionLock")
    @Transactional
    public void versionLock() {
        coderService.raiseSalaryVersionLock(1L, 1000D);
    }

    @GetMapping("/tableLock")
    @Transactional
    public void tableLock() {
        coderService.findBySex();
        try {
            TimeUnit.SECONDS.sleep(5L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/rowLock/{id}")
    @Transactional()
    public void findById(@PathVariable Long id) {
        coderService.findCoderById(id);

        // 对照测试 如果查询1的话就暂停 5秒
        try {
            if (id == 1L) {
                TimeUnit.SECONDS.sleep(5L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
