package br.com.fiap.restaurante.core.controller;

import br.com.fiap.restaurante.core.dto.CreatePersonInputDto;
import br.com.fiap.restaurante.core.dto.PersonOutputDto;
import br.com.fiap.restaurante.core.dto.UpdatePersonInputDto;
import br.com.fiap.restaurante.core.usecase.CreatePersonUseCase;
import br.com.fiap.restaurante.core.usecase.DeletePersonUseCase;
import br.com.fiap.restaurante.core.usecase.GetPersonByIdUseCase;
import br.com.fiap.restaurante.core.usecase.GetPersonsUseCase;
import br.com.fiap.restaurante.core.usecase.UpdatePersonUseCase;

import java.util.List;

public class PersonController {

    private final CreatePersonUseCase createPersonUseCase;
    private final GetPersonsUseCase getPersonsUseCase;
    private final GetPersonByIdUseCase getPersonByIdUseCase;
    private final UpdatePersonUseCase updatePersonUseCase;
    private final DeletePersonUseCase deletePersonUseCase;

    public PersonController(
            CreatePersonUseCase createPersonUseCase,
            GetPersonsUseCase getPersonsUseCase,
            GetPersonByIdUseCase getPersonByIdUseCase,
            UpdatePersonUseCase updatePersonUseCase,
            DeletePersonUseCase deletePersonUseCase
    ) {
        this.createPersonUseCase = createPersonUseCase;
        this.getPersonsUseCase = getPersonsUseCase;
        this.getPersonByIdUseCase = getPersonByIdUseCase;
        this.updatePersonUseCase = updatePersonUseCase;
        this.deletePersonUseCase = deletePersonUseCase;
    }

    public PersonOutputDto create(CreatePersonInputDto input) {
        return createPersonUseCase.execute(input);
    }

    public List<PersonOutputDto> findAll() {
        return getPersonsUseCase.execute();
    }

    public PersonOutputDto findById(Long id) {
        return getPersonByIdUseCase.execute(id);
    }

    public PersonOutputDto update(UpdatePersonInputDto input) {
        return updatePersonUseCase.execute(input);
    }

    public void delete(Long id) {
        deletePersonUseCase.execute(id);
    }
}
