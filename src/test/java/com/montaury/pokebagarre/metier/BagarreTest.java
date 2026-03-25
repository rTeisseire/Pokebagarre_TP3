/**
 * Classe de test pour la méthode demarrer de la classe Bagarre.
 * * Liste des tests :
 * - Le nom du premier Pokémon renseigner est null.
 * - Le nom du premier Pokémon renseigner est vide.
 * - Le nom du second Pokémon renseigner est null.
 * - Le nom du second Pokémon renseigner est vide.
 * - Les noms des deux Pokémons renseigner sont les mêmes.
 * - L'API renvoie une erreur de récupération de nom pour le Pokémon 1.
 * - L'API renvoie une erreur de récupération de nom pour le Pokémon 2.
 * - Le premier Pokémon est retourné si il est gagnant.
 * - Le second Pokémon est retourné si il est gagnant.
 */

package com.montaury.pokebagarre.metier;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.montaury.pokebagarre.erreurs.*;

import com.montaury.pokebagarre.webapi.PokeBuildApi;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class BagarreTest {

    private Bagarre bagarre;

    @Test
    public void demarrer_devrai_lever_erreur_si_premier_pokemon_est_null()
    {
        //Given
        bagarre = new Bagarre();

        //When
        Throwable thrown = catchThrowable(() -> bagarre.demarrer(null, "Lockpin"));

        //Then
        assertThat(thrown)
         .isInstanceOf(ErreurPokemonNonRenseigne.class)
         .hasMessage("Le premier pokemon n'est pas renseigne");
    }

    @Test
    public void demarrer_devrai_lever_erreur_si_premier_pokemon_est_vide()
    {
        //Given
        bagarre = new Bagarre();

        //When
        Throwable thrown = catchThrowable(() -> bagarre.demarrer("", "Lucario"));

        //Then
        assertThat(thrown)
                .isInstanceOf(ErreurPokemonNonRenseigne.class)
                .hasMessage("Le premier pokemon n'est pas renseigne");
    }

    @Test
    public void demarrer_devrai_lever_erreur_si_deuxieme_pokemon_est_null()
    {
        //Given
        bagarre = new Bagarre();

        //When
        Throwable thrown = catchThrowable(() -> bagarre.demarrer("Mew", null));

        //Then
        assertThat(thrown)
                .isInstanceOf(ErreurPokemonNonRenseigne.class)
                .hasMessage("Le second pokemon n'est pas renseigne");
    }

    @Test
    public void demarrer_devrai_lever_erreur_si_deuxieme_pokemon_est_vide()
    {
        //Given
        bagarre = new Bagarre();

        //When
        Throwable thrown = catchThrowable(() -> bagarre.demarrer("Carapuce", ""));

        //Then
        assertThat(thrown)
                .isInstanceOf(ErreurPokemonNonRenseigne.class)
                .hasMessage("Le second pokemon n'est pas renseigne");
    }

    @Test
    public void demarrer_devrai_lever_erreur_si_deux_pokemons_ont_le_meme_nom()
    {
        //Given
        bagarre = new Bagarre();

        //When
        Throwable thrown = catchThrowable(() -> bagarre.demarrer("Rayquaza", "Rayquaza"));

        //Then
        assertThat(thrown)
                .isInstanceOf(ErreurMemePokemon.class)
                .hasMessage("Impossible de faire se bagarrer un pokemon avec lui-meme");
    }

    @Test
    public void demarrer_devrai_lever_erreur_si_API_renvoie_une_erreur_de_recuperation_de_nom_pour_le_pokemon_1()
    {
        //Given
        var fausseApi = Mockito.mock(PokeBuildApi.class) ;
        bagarre = new Bagarre(fausseApi);

        Mockito.when(fausseApi.recupererParNom("goku"))
                .thenReturn(CompletableFuture.failedFuture(new ErreurRecuperationPokemon("goku"))
                );

        Mockito.when(fausseApi.recupererParNom("Félinferno"))
                .thenReturn(CompletableFuture.completedFuture(new Pokemon("Félinferno", "url1",
                        new Stats(1, 2))
                ));

        //When
        CompletableFuture<Pokemon> futurVainqueur = bagarre.demarrer("goku", "Félinferno");

        //Then
        assertThat(futurVainqueur)
                .failsWithin(Duration.ofSeconds(2))
                .withThrowableOfType(ExecutionException.class)
                .havingCause()
                .isInstanceOf(ErreurRecuperationPokemon.class)
                .withMessage("Impossible de recuperer les details sur 'goku'") ;
    }

    @Test
    public void demarrer_devrai_lever_erreur_si_API_renvoie_une_erreur_de_recuperation_de_nom_pour_le_pokemon_2()
    {
        //Given
        var fausseApi = Mockito.mock(PokeBuildApi.class) ;
        bagarre = new Bagarre(fausseApi);

        Mockito.when(fausseApi.recupererParNom("Nymphali"))
                .thenReturn(CompletableFuture.completedFuture(new Pokemon("Nymphali", "url1",
                        new Stats(1, 2))
                ));

        Mockito.when(fausseApi.recupererParNom("vegeta"))
                .thenReturn(CompletableFuture.failedFuture(new ErreurRecuperationPokemon("vegeta"))
                );

        //When
        CompletableFuture<Pokemon> futurVainqueur = bagarre.demarrer("Nymphali", "vegeta");

        //Then
        assertThat(futurVainqueur)
                .failsWithin(Duration.ofSeconds(2))
                .withThrowableOfType(ExecutionException.class)
                .havingCause()
                .isInstanceOf(ErreurRecuperationPokemon.class)
                .withMessage("Impossible de recuperer les details sur 'vegeta'") ;
    }

    @Test
    public void demarrer_devrai_retourner_le_pokemon_1_si_il_est_gagnant()
    {
        //Given
        Pokemon pokemon1 = new Pokemon("Artikodin", "url1", new Stats(10, 20));
        Pokemon pokemon2 = new Pokemon("Sulfura", "url1", new Stats(1, 2));
        var fausseApi = Mockito.mock(PokeBuildApi.class) ;
        bagarre = new Bagarre(fausseApi);

        Mockito.when(fausseApi.recupererParNom("Artikodin"))
                .thenReturn(CompletableFuture.completedFuture(pokemon1)
                );

        Mockito.when(fausseApi.recupererParNom("Sulfura"))
                .thenReturn(CompletableFuture.completedFuture(pokemon2)
                );

        //When
        CompletableFuture<Pokemon> futurVainqueur = bagarre.demarrer("Artikodin", "Sulfura");

        //Then
        assertThat(futurVainqueur)
                .succeedsWithin(Duration.ofSeconds(2))
                .satisfies (pokemon -> {
                            assertThat(pokemon.getNom())
                                    .isEqualTo("Artikodin"); // autres assertions...
                        }
                ) ;

    }

    @Test
    public void demarrer_devrai_lever_le_pokemon_2_si_il_est_gagnant()
    {
        //Given
        Pokemon pokemon1 = new Pokemon("Poussifeu", "url1", new Stats(1, 2));
        Pokemon pokemon2 = new Pokemon("Ectoplasma", "url1", new Stats(10, 20));
        var fausseApi = Mockito.mock(PokeBuildApi.class) ;
        bagarre = new Bagarre(fausseApi);

        Mockito.when(fausseApi.recupererParNom("Poussifeu"))
                .thenReturn(CompletableFuture.completedFuture(pokemon1)
                );

        Mockito.when(fausseApi.recupererParNom("Ectoplasma"))
                .thenReturn(CompletableFuture.completedFuture(pokemon2)
                );

        //When
        CompletableFuture<Pokemon> futurVainqueur = bagarre.demarrer("Poussifeu", "Ectoplasma");

        //Then
        assertThat(futurVainqueur)
                .succeedsWithin(Duration.ofSeconds(2))
                .satisfies (pokemon -> {
                            assertThat(pokemon.getNom())
                                    .isEqualTo("Ectoplasma"); // autres assertions...
                        }
                ) ;

    }
}
