/**
 * Classe de test pour la méthode estVainqueurContre de la classe Pokemon.
 * * Liste des tests :
 * - Le premier Pokémon gagne s'il a une meilleure attaque.
 * - Le second Pokémon gagne s'il a une meilleure attaque.
 * - Le premier Pokémon gagne s'il a la même attaque mais une meilleure défense.
 * - Le second Pokémon gagne s'il a la même attaque mais une meilleure défense.
 * - Le premier Pokémon gagne par défaut s'ils ont les mêmes statistiques.
 */

package com.montaury.pokebagarre.metier;

import com.montaury.pokebagarre.fixtures.ConstructeurDePokemon;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PokemonTest {

    @Test
    public void premier_serait_vainqueur_avec_meilleur_attaque(){
        // Given
        Pokemon pokemon1 = ConstructeurDePokemon.unPokemon().avecAttaque(500).avecDefense(400).construire();
        Pokemon pokemon2 = ConstructeurDePokemon.unPokemon().avecAttaque(400).avecDefense(400).construire();

        // When
        boolean resultat = pokemon1.estVainqueurContre(pokemon2);

        // Then
        assertThat(resultat).isTrue();

    }

    @Test
    public void second_serait_vainqueur_avec_meilleur_attaque(){
        // Given
        Pokemon pokemon1 = ConstructeurDePokemon.unPokemon().avecAttaque(400).avecDefense(400).construire();
        Pokemon pokemon2 = ConstructeurDePokemon.unPokemon().avecAttaque(500).avecDefense(400).construire();

        // When
        boolean resultat = pokemon1.estVainqueurContre(pokemon2);

        // Then
        assertThat(resultat).isFalse();
    }
}
