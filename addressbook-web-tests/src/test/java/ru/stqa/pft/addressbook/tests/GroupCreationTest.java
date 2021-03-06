package ru.stqa.pft.addressbook.tests;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.xstream.XStream;
import org.testng.annotations.*;
import ru.stqa.pft.addressbook.model.GroupData;
import ru.stqa.pft.addressbook.model.Groups;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GroupCreationTest extends TestBase {


    /**
     * Позволяет загружать данные для теста из файла в формате xml
     */
    @DataProvider
    public Iterator<Object[]> validGroupsFromXml() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("src/test/resources/groups.xml")))) {
            String xml = "";

            String line = reader.readLine();
            while (line != null) {
                xml += line;
                line = reader.readLine();
            }
            XStream xStream = new XStream();
            xStream.processAnnotations(GroupData.class);
            List<GroupData> groups = (List<GroupData>) xStream.fromXML(xml);
            return groups.stream().map((g) -> new Object[]{g}).collect(Collectors.toList()).iterator();
        }
    }

    /**
     * Позволяет загружать данные для теста из файла в формате json
     */
    @DataProvider
    public Iterator<Object[]> validGroupsFromJson() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("src/test/resources/groups.json")))) {
            String json = "";

            String line = reader.readLine();
            while (line != null) {
                json += line;
                line = reader.readLine();
            }
            Gson gson = new Gson();
            List<GroupData> groups = gson.fromJson(json, new TypeToken<List<GroupData>>() {
            }.getType());  // List<GroupData>.class
            return groups.stream().map((g) -> new Object[]{g}).collect(Collectors.toList()).iterator();
        }

    }


    /**
     * Тест проверяет корректное создание группы
     */
    @Test(dataProvider = "validGroupsFromJson")
    public void testGroupCreation(GroupData group) throws Exception {
        app.goTo().groupPage();

        logger.info("Формируется список групп до создания новой");
        Groups before = app.db().groups();

        logger.info("Происходит создание новой группы");
        app.group().create(group);

        logger.info("Сравнивается кол-во групп до и после создания");
        assertThat(app.group().count(), equalTo(before.size() + 1));

        logger.info("Формируется список групп после создания");
        Groups after = app.db().groups();

        logger.info("Сравнивается список групп до и после создания");
        assertThat(after, equalTo(
                before.withAdded(group.withId(after.stream().mapToInt((g) -> g.getId()).max().getAsInt()))));

        verifyGroupListInUI();
    }

    /**
     * Тест проверяет, что нельзя создать группу с запрещённым символом '
     */
    @Test
    public void testBadGroupCreation() throws Exception {
        app.goTo().groupPage();

        logger.info("Формируется список групп до создания новой");
        Groups before = app.db().groups();

        logger.info("Попытка добавить группу с некорректным наименованием");
        GroupData group = new GroupData().withName("testName'").withFooter("testFooter").withHeader("testHeader");
        app.group().create(group);

        logger.info("Сравнивается кол-во групп до и после попытки добавления");
        assertThat(app.group().count(), equalTo(before.size()));
        Groups after = app.db().groups();

        logger.info("Сравнивается список групп до и после создания");
        assertThat(after, equalTo(before));
    }

}
