
package it.org.apache.struts2.showcase;

public class ConversionTest extends ITBaseTest {
    public void testList() {
        beginAt("/conversion/enterPersonsInfo.action");
        setTextField("persons[0].name", "name0");
        setTextField("persons[0].age", "0");
        setTextField("persons[1].name", "name1");
        setTextField("persons[1].age", "1");
        setTextField("persons[2].name", "name2");
        setTextField("persons[2].age", "2");

        submit();

        assertTextPresent("SET 0 Name: name0");
        assertTextPresent("SET 0 Age: 0");
        assertTextPresent("SET 1 Name: name1");
        assertTextPresent("SET 1 Age: 1");
        assertTextPresent("SET 2 Name: name2");
        assertTextPresent("SET 2 Age: 2");
    }

    public void testSet() {
        beginAt("/conversion/enterAddressesInfo.action");
        setTextField("addresses('id0').address", "address0");
        setTextField("addresses('id1').address", "address1");
        setTextField("addresses('id2').address", "address2");

        submit();

        assertTextPresent("id0 -> address0");
        assertTextPresent("id1 -> address1");
        assertTextPresent("id2 -> address2");
    }

    public void testEnum() {
        beginAt("/conversion/enterOperationEnumInfo.action");
        checkCheckbox("selectedOperations", "ADD");
        checkCheckbox("selectedOperations", "MINUS");

        submit();

        assertTextPresent("ADD");
        assertTextPresent("MINUS");        
    }
}

