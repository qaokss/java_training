package ru.stqa.pft.addressbook.tests;


import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.stqa.pft.addressbook.model.ContactData;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

public class ContactPhoneTest extends TestBase {

    @BeforeMethod
    public void EnsurePreconditions() {
        app.goTo().homePage();
        // если на странице нет ни одного созданного контакта - создаём его
        if (app.contact().allContacts().size() == 0) {
            app.goTo().addContactPage();
            app.contact().createNew(new ContactData().withFirstname("TestFirtsName").withMiddlename("Middlename").
                    withLastname("Lastname").withAddress("adr").withMobilePhone("4654654").withEmail1("London@fg.tyh")
                    .withBirthdayDay("10").withBirthdayMonth("January").withBirthdayYear("2000"), true);
        }
    }
    /**
     * тест проверяет соответствие телефонов контакта  на форме редактирования и главной странице
     */
    @Test
    public void testContactPhone() {
        app.goTo().homePage();
        logger.info("Формируется список контактов на главной странице");
        ContactData contact = app.contact().allContacts().iterator().next();

        logger.info("Осуществляется переход на форму редактирования контакта и собирается информация на форме редактирования");
        ContactData contactInfoFromEditForm = app.contact().infoFromEditForm(contact);

        logger.info("Сравнивается инфа на главной странице с инфой на странице редактирования");
        assertThat(contact.getAllPhones(), equalTo(mergePhones(contactInfoFromEditForm)));
    }

    /**
     * метод склеивает имеющиеся значения телефонов
     */
    private String mergePhones(ContactData contact) {
       return Arrays.asList(contact.getHomePhone(), contact.getMobilePhone(), contact.getWorkPhone()).stream().
                filter((String s) -> ! s.equals("")).map(ContactPhoneTest::cleaned).collect(Collectors.joining("\n"));
    }
    /**
     * метод очищает лишние символы, приводит в необходимый вид
     */
    public static String cleaned(String phone) {
        return phone.replaceAll("\\s", "").replaceAll("[-()]", "");
    }
}
