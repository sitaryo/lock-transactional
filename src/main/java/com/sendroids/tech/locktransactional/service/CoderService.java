package com.sendroids.tech.locktransactional.service;

import com.sendroids.tech.locktransactional.entity.Coder;
import com.sendroids.tech.locktransactional.entity.CoderRepository;
import com.sendroids.tech.locktransactional.exception.CoderNotFoundException;
import com.sendroids.tech.locktransactional.exception.CoderSalaryException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collection;
import java.util.Optional;

@Service
public class CoderService {
    private final CoderRepository coderRepository;

    public CoderService(
            CoderRepository coderRepository
    ) {
        this.coderRepository = coderRepository;
    }


    public Optional<Coder> findCoderById(long id) {
        return coderRepository.findById(id);
    }

    @Transactional
    public void raiseSalaryTransactional(long id, double salary) {
        this.raise(id, salary);
    }

    public void raiseSalaryNoTransactional(long id, double salary) {
        this.raise(id, salary);
    }

    private void raise(long id, double salary) {
        var coder = coderRepository.findById(id)
                .orElseThrow(CoderNotFoundException::new);

        coder.setSalary(coder.getSalary() + salary);
        coderRepository.save(coder);
    }

    public void payCut(long id, double salary) {
        var coder = coderRepository.findById(id)
                .orElseThrow(CoderNotFoundException::new);

        if (coder.getSalary() - salary <= 0) {
            throw new CoderSalaryException("salary is too low");
        }

        coder.setSalary(coder.getSalary() + salary);
        coderRepository.save(coder);
    }

    @Transactional
    public void payCutTransactional(long id, double salary) {
        var coder = coderRepository.findById(id)
                .orElseThrow(CoderNotFoundException::new);

        if (coder.getSalary() - salary <= 0) {
            throw new CoderSalaryException("salary is too low");
        }

        coder.setSalary(coder.getSalary() + salary);
        coderRepository.save(coder);
    }

    public void raiseSalaryVersionLock(long id, double salary) {
        var coder = coderRepository.findByIdAndAge(id, 18)
                .orElseThrow(CoderNotFoundException::new);

        coder.setSalary(coder.getSalary() + salary);
        coderRepository.save(coder);
    }

    public Collection<Coder> findBySex(){
        return coderRepository.findBySex(1);
    }
}