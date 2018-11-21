package br.edu.ulbra.election.election.client;

import br.edu.ulbra.election.election.output.v1.VoterOutput;
import br.edu.ulbra.election.election.model.Voter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class VoterClientService {

    private final VoterClient voterClient;

    @Autowired
    public VoterClientService(VoterClient voterClient){
        this.voterClient = voterClient;
    }

    public VoterOutput getById(Long id){
        return this.voterClient.getById(id);
    }

    @FeignClient(value="election-service", url="${url.election-service}")
    private interface VoterClient {

        @GetMapping("/v1/election/{electionId}")
        VoterOutput getById(@PathVariable(name = "electionId") Long electionId);
    }
}
