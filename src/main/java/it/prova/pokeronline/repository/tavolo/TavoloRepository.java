package it.prova.pokeronline.repository.tavolo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.model.Utente;

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

	@Query(value = "SELECT t.* " + "	FROM tavolo AS t " + "	JOIN tavolo_giocatori AS tg ON t.id = tg.tavolo_id "
			+ "	JOIN utente AS u ON tg.giocatori_id = u.id " + "	GROUP BY t.id "
			+ "	ORDER BY SUM(u.esperienzaaccumulata) DESC " + "	LIMIT 1", nativeQuery = true)
	Tavolo trovaTavoloConMassimaEsperienzaGiocatori();

	@Modifying
	@Query(value = "delete g.* from tavolo_giocatori g inner join tavolo t on t.id = g.Tavolo_id inner join utente u on u.id = g.giocatori_id\r\n"
			+ "where g.Tavolo_id in (select u.id from utente u where u.username in :listaUsername);", nativeQuery = true)
	public void svuotaTavoliCreatiDaUtenti(List<String> listaUsername);

	@Query(value = "SELECT distinct u.*\r\n" + "	FROM utente u\r\n"
			+ "	inner JOIN tavolo t ON u.id = t.utente_id\r\n"
			+ "	WHERE t.datacreazione < u.dataregistrazione", nativeQuery = true)
	List<Utente> listaUtentiDateSbagliate();

}
