
package it.org.apache.struts2.showcase;

public class UITagExampleTest extends ITBaseTest {
    public void testInputForm() {
        setScriptingEnabled(false);
        beginAt("/tags/ui/example!input.action");
        assertFormPresent("exampleSubmit");
        
        assertFormElementPresent("name");
        
        assertFormElementPresent("bio");
        
        assertFormElementPresent("favouriteColor");
        
        assertFormElementPresent("friends");
        
        assertFormElementPresent("legalAge");

        
        setTextField("name", "name");
        setTextField("bio", "bio");
        selectOption("favouriteColor", "Red");
        checkCheckbox("friends", "Patrick");
        checkCheckbox("friends", "Jason");
        checkCheckbox("legalAge");

        submit();

        assertTextInElement("name", "name");
        assertTextInElement("bio", "bio");
        assertTextInElement("favouriteColor", "Red");
        assertTextInElement("friends", "[Patrick, Jason]");
        assertTextInElement("legalAge", "true");
    }
}
