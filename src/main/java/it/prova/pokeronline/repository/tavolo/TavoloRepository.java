package it.prova.pokeronline.repository.tavolo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.prova.pokeronline.model.Tavolo;

public interface TavoloRepository extends CrudRepository<Tavolo, Long> {

	@Query("select t from Tavolo t left join fetch t.utenteCreazione left join fetch t.giocatori")
	List<Tavolo> findAllEager();

	@Query("select t from Tavolo t left join fetch t.utenteCreazione left join fetch t.giocatori where t.id = ?1")
	Tavolo findByIdEager(Long id);

	Optional<Tavolo> findByDenominazione(String denominazione);

	Optional<Tavolo> findByGiocatori_id(Long id);

	@Query("select t from Tavolo t join t.utenteCreazione where t.utenteCreazione.id = ?1")
	List<Tavolo> findAllSpecialPlayer(Long id);

	@Query("select t from Tavolo t join t.utenteCreazione where t.id = ?1 and t.utenteCreazione.id = ?2")
	Optional<Tavolo> findByIdSpecialPlayer(Long idTavolo, Long idUtente);

	List<Tavolo> findByEsperienzaMinimaLessThan(Integer esperienzaAccumulata);

	@Query(value = "select t.* from tavolo t " + "where t.utente_id = :idInSessione and exists"
			+ "(select * from tavolo_giocatori p inner join utente u " + "on p.giocatori_id = u.id where "
			+ "p.tavolo_id = t.id and u.esperienzaaccumulata >= :soglia)", nativeQuery = true)
	List<Tavolo> estraiTavoliConAlmenoUnUtenteAlDiSopraDiSoglia(@Param("idInSessione") Long idInSessione,
			@Param("soglia") Integer soglia);

}
