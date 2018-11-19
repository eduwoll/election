package br.edu.ulbra.election.election.repository;

import br.edu.ulbra.election.election.model.Voter;
import org.springframework.data.repository.CrudRepository;

public interface VoterRepository extends CrudRepository<Voter, Long> {

    Voter findFirstByEmail(String email);

}
