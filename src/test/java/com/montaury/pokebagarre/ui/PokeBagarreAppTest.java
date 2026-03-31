/**
 * Classe de test pour la méthode start de la classe PokeBagarreApp.
 * * Liste des tests :
 * - Le nom du premier Pokémon n'est pas renseigné.
 * - Le nom du second Pokémon n'est pas renseigné.
 * - Aucun nom de Pokémon n'est renseigné.
 * - Les noms des deux Pokémons renseigner sont les mêmes.
 * - L'API renvoie une erreur de récupération de nom pour le Pokémon 1.
 * - L'API renvoie une erreur de récupération de nom pour le Pokémon 2.
 *   Le "Le vainqueur est:" est retourné si les deux sont bons.
 * - Le nom de Pokémon gagnant est retourné si les deux sont bons.
 */

package com.montaury.pokebagarre.ui;

import java.util.concurrent.TimeUnit;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@ExtendWith(ApplicationExtension.class)
class PokeBagarreAppTest {

    private static final String IDENTIFIANT_CHAMP_DE_SAISIE_POKEMON_1 = "#nomPokemon1";
    private static final String IDENTIFIANT_CHAMP_DE_SAISIE_POKEMON_2 = "#nomPokemon2";
    private static final String IDENTIFIANT_BOUTON_BAGARRE = ".button";

    @Start
    private void start(Stage stage) {
        new PokeBagarreApp().start(stage);
    }

    @Test
    void devrait_afficher_erreur_si_premier_pokemon_non_renseigne(FxRobot robot) {
        // Given
        robot.clickOn(IDENTIFIANT_CHAMP_DE_SAISIE_POKEMON_2);
        robot.write("Mackogneur");

        // When
        robot.clickOn(IDENTIFIANT_BOUTON_BAGARRE);

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(getMessageErreur(robot))
                        .isEqualTo("Erreur: Le premier pokemon n'est pas renseigne")
        );
    }

    @Test
    void devrait_afficher_erreur_si_second_pokemon_non_renseigne(FxRobot robot) {
        // Given
        robot.clickOn(IDENTIFIANT_CHAMP_DE_SAISIE_POKEMON_1);
        robot.write("Férosinge");

        // When
        robot.clickOn(IDENTIFIANT_BOUTON_BAGARRE);

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(getMessageErreur(robot))
                        .isEqualTo("Erreur: Le second pokemon n'est pas renseigne")
        );
    }

    @Test
    void devrait_afficher_erreur_si_aucun_pokemon_renseigne(FxRobot robot) {
        // Given

        // When
        robot.clickOn(IDENTIFIANT_BOUTON_BAGARRE);

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(getMessageErreur(robot))
                        .isEqualTo("Erreur: Le premier pokemon n'est pas renseigne")
        );
    }

    @Test
    void devrait_afficher_erreur_si_les_deux_pokemons_sont_identiques(FxRobot robot) {
        // Given
        robot.clickOn(IDENTIFIANT_CHAMP_DE_SAISIE_POKEMON_1);
        robot.write("Hypnomade");
        robot.clickOn(IDENTIFIANT_CHAMP_DE_SAISIE_POKEMON_2);
        robot.write("Hypnomade");

        // When
        robot.clickOn(IDENTIFIANT_BOUTON_BAGARRE);

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(getMessageErreur(robot))
                        .isEqualTo("Erreur: Impossible de faire se bagarrer un pokemon avec lui-meme")
        );
    }

    @Test
    void devrait_afficher_erreur_si_premier_pokemon_inexistant(FxRobot robot) {
        // Given
        robot.clickOn(IDENTIFIANT_CHAMP_DE_SAISIE_POKEMON_1);
        robot.write("freezer");
        robot.clickOn(IDENTIFIANT_CHAMP_DE_SAISIE_POKEMON_2);
        robot.write("Pikachu");

        // When
        robot.clickOn(IDENTIFIANT_BOUTON_BAGARRE);

        // Then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(getMessageErreur(robot))
                        .startsWith("Erreur:")
        );
    }

    @Test
    void devrait_afficher_erreur_si_second_pokemon_inexistant(FxRobot robot) {
        // Given
        robot.clickOn(IDENTIFIANT_CHAMP_DE_SAISIE_POKEMON_1);
        robot.write("Psykokwak");
        robot.clickOn(IDENTIFIANT_CHAMP_DE_SAISIE_POKEMON_2);
        robot.write("picollo");

        // When
        robot.clickOn(IDENTIFIANT_BOUTON_BAGARRE);

        // Then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(getMessageErreur(robot))
                        .startsWith("Erreur:")
        );
    }

    @Test
    void devrait_afficher_le_vainqueur_pour_deux_pokemons_valides(FxRobot robot) {
        // Given
        robot.clickOn(IDENTIFIANT_CHAMP_DE_SAISIE_POKEMON_1);
        robot.write("Latias");
        robot.clickOn(IDENTIFIANT_CHAMP_DE_SAISIE_POKEMON_2);
        robot.write("Aurorus");

        // When
        robot.clickOn(IDENTIFIANT_BOUTON_BAGARRE);

        // Then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(getResultatBagarre(robot))
                        .startsWith("Le vainqueur est:")
        );
    }

    @Test
    void devrait_afficher_le_nom_du_vainqueur_parmi_les_deux_pokemons(FxRobot robot) {
        // Given
        robot.clickOn(IDENTIFIANT_CHAMP_DE_SAISIE_POKEMON_1);
        robot.write("Miraidon");
        robot.clickOn(IDENTIFIANT_CHAMP_DE_SAISIE_POKEMON_2);
        robot.write("Braixen");

        // When
        robot.clickOn(IDENTIFIANT_BOUTON_BAGARRE);

        // Then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            String resultat = getResultatBagarre(robot).toLowerCase();
            assertThat(resultat).satisfiesAnyOf(
                    r -> assertThat(r).contains("miraidon"),
                    r -> assertThat(r).contains("braixen")
            );
        });
    }

    private static String getResultatBagarre(FxRobot robot) {
        return robot.lookup("#resultatBagarre").queryText().getText();
    }

    private static String getMessageErreur(FxRobot robot) {
        return robot.lookup("#resultatErreur").queryLabeled().getText();
    }
}

