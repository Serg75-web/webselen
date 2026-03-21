import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$;

public class CardDeliveredTest {

    private String generateDate(String pattern) {
        return LocalDate.now().plusDays(4).format(DateTimeFormatter.ofPattern(pattern));
    }

    @BeforeEach
    void setup() {
        Selenide.open("http://localhost:9999/");
    }

    @Test
    void shouldFillValid () { //валидные данные

        $("[data-test-id='city']").shouldBe(Condition.visible).click();
        $("[data-test-id='city'] input").setValue("Петрозаводск");
        String planningDate = generateDate("dd.MM.yyyy");
        $("[data-test-id='date'] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(planningDate);
        $("[name='name']").setValue("Иван Иванов");
        $("[name='phone']").setValue("+79208882134");
        $("[data-test-id='agreement']").click();
        $("button .button__text").shouldHave(Condition.text("Забронировать")).click();

        $("[data-test-id='notification']").shouldHave(Condition.text("Успешно! Встреча успешно забронирована на " + planningDate)
                , Duration.ofSeconds(15)).shouldBe(Condition.visible);

    }

    @Test
    void shouldCityInvalid () { // невалидный город

        $("[data-test-id='city']").shouldBe(Condition.visible).click();
        $("[data-test-id='city'] input").setValue("Moscow");
        String planningDate = generateDate("dd.MM.yyyy");
        $("[data-test-id='date'] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(planningDate);
        $("[name='name']").setValue("Иван Иванов");
        $("[name='phone']").setValue("+79208882134");
        $("[data-test-id='agreement']").click();
        $("button .button__text").shouldHave(Condition.text("Забронировать")).click();

        $("[data-test-id='city'] .input__sub").shouldHave(Condition.text("Доставка в выбранный город недоступна"));

    }

    @Test
    void shouldDateInvalid () { // невалидная дата

        $("[data-test-id='city']").shouldBe(Condition.visible).click();
        $("[data-test-id='city'] input").setValue("Петрозаводск");
        String planningDate = generateDate("dd.MM");
        $("[data-test-id='date'] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(planningDate);
        $("[name='name']").setValue("Иван Иванов");
        $("[name='phone']").setValue("+79208882134");
        $("[data-test-id='agreement']").click();
        $("button .button__text").shouldHave(Condition.text("Забронировать")).click();

        $("[data-test-id='date'] .input__sub").shouldHave(Condition.text("Неверно введена дата"));

    }

    @Test
    void shouldNameInvalid () { // невалидные фамилия и имя

        $("[data-test-id='city']").shouldBe(Condition.visible).click();
        $("[data-test-id='city'] input").setValue("Петрозаводск");
        String planningDate = generateDate("dd.MM.yyyy");
        $("[data-test-id='date'] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(planningDate);
        $("[name='name']").setValue("Ivan Ivanov");
        $("[name='phone']").setValue("+79208882134");
        $("[data-test-id='agreement']").click();
        $("button .button__text").shouldHave(Condition.text("Забронировать")).click();

        $("[data-test-id='name'] .input__sub").shouldHave(Condition.text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));

    }

    @Test
    void shouldPhoneInvalid () { // невалидный номер телефона

        $("[data-test-id='city']").shouldBe(Condition.visible).click();
        $("[data-test-id='city'] input").setValue("Петрозаводск");
        String planningDate = generateDate("dd.MM.yyyy");
        $("[data-test-id='date'] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(planningDate);
        $("[name='name']").setValue("Иван Иванов");
        $("[name='phone']").setValue("Иван Иванов");
        $("[data-test-id='agreement']").click();
        $("button .button__text").shouldHave(Condition.text("Забронировать")).click();

        $("[data-test-id='phone'] .input__sub").shouldHave(Condition.text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));

    }

    @Test
    void shouldNotClicked () { // неотжатый чекбокс

        $("[data-test-id='city']").shouldBe(Condition.visible).click();
        $("[data-test-id='city'] input").setValue("Петрозаводск");
        $("[data-test-id='date']").shouldBe(Condition.visible).click();
        $("[data-test-id='date'] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue("27032026");
        $("[name='name']").setValue("Иван Иванов");
        $("[name='phone']").setValue("+79208882134");
//        $("[data-test-id='agreement']").click();
        $("button .button__text").shouldHave(Condition.text("Забронировать")).click();

        $("[data-test-id='agreement'] .checkbox__text").shouldHave(Condition.text("Я соглашаюсь с условиями обработки и использования моих персональных данных"));

        String expectedColor = "rgba(255, 92, 92, 1)"; // преобразовали hex #ff5c5c в rgb
        $("[data-test-id='agreement'] .checkbox__text").shouldHave(Condition.cssValue("color", expectedColor));

    }
}
