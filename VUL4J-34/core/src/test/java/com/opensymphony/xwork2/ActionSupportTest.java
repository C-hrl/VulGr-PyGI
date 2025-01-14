
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;

import java.util.*;


public class ActionSupportTest extends XWorkTestCase {

    private ActionSupport as;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        as = new ActionSupport();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        as = null;
    }

    public void testNothingDoneOnActionSupport() throws Exception {
        assertEquals(false, as.hasErrors());

        assertNotNull(as.getActionErrors());
        assertEquals(0, as.getActionErrors().size());
        assertEquals(false, as.hasActionErrors());

        assertNotNull(as.getActionMessages());
        assertEquals(0, as.getActionMessages().size());
        assertEquals(false, as.hasActionMessages());

        assertNotNull(as.getFieldErrors());
        assertEquals(0, as.getFieldErrors().size());
        assertEquals(false, as.hasFieldErrors());

        assertNull(as.getText(null));

        try {
            as.pause(null);
        } catch (Exception e) {
            fail("Should not fail");
        }

        assertEquals(Action.INPUT, as.input());
        assertEquals(Action.SUCCESS, as.doDefault());
        assertEquals(Action.SUCCESS, as.execute());
        try {
            as.clone();
            fail("Failure expected for clone()");
        } catch (CloneNotSupportedException e) {
            
        }


        assertNull(as.getText(null, (List) null));
        assertNull(as.getText(null, (String) null));
        assertNull(as.getText(null, (String[]) null));

        assertNull(as.getText(null, (String) null, (List) null));
        assertNull(as.getText(null, (String) null, (String) null));
        assertNull(as.getText(null, (String) null, (String[]) null));

        assertNull(as.getText(null, (String) null, (List) null, (ValueStack) null));
        assertNull(as.getText(null, (String) null, (String[]) null, (ValueStack) null));

        assertNotNull(as.getLocale());
        assertEquals(ActionContext.getContext().getLocale(), as.getLocale());

        assertNull(as.getTexts()); 
        assertEquals("not.in.bundle", as.getText("not.in.bundle"));
    }

    public void testActionErrors() {
        assertEquals(false, as.hasActionErrors());
        assertEquals(0, as.getActionErrors().size());
        as.addActionError("Damm");
        assertEquals(1, as.getActionErrors().size());
        assertEquals("Damm", as.getActionErrors().iterator().next());
        assertEquals(true, as.hasActionErrors());
        assertEquals(true, as.hasErrors());

        as.clearErrorsAndMessages();
        assertEquals(false, as.hasActionErrors());
        assertEquals(false, as.hasErrors());
    }

    public void testActionMessages() {
        assertEquals(false, as.hasActionMessages());
        assertEquals(0, as.getActionMessages().size());
        as.addActionMessage("Killroy was here");
        assertEquals(1, as.getActionMessages().size());
        assertEquals("Killroy was here", as.getActionMessages().iterator().next());
        assertEquals(true, as.hasActionMessages());

        assertEquals(false, as.hasActionErrors()); 
        assertEquals(false, as.hasErrors()); 

        as.clearErrorsAndMessages();
        assertEquals(false, as.hasActionMessages());
        assertEquals(false, as.hasErrors());
    }

    public void testFieldErrors() {
        assertEquals(false, as.hasFieldErrors());
        assertEquals(0, as.getFieldErrors().size());
        as.addFieldError("username", "Admin is not allowed as username");
        List<String> errors = as.getFieldErrors().get("username");
        assertEquals(1, errors.size());
        assertEquals("Admin is not allowed as username", errors.get(0));

        assertEquals(true, as.hasFieldErrors());
        assertEquals(true, as.hasErrors());

        as.clearErrorsAndMessages();
        assertEquals(false, as.hasFieldErrors());
        assertEquals(false, as.hasErrors());
    }

    public void testLocale() {
        Locale defLocale = Locale.getDefault();
        ActionContext.getContext().setLocale(null);

        
        assertNotNull(as.getLocale());
        assertEquals(defLocale, as.getLocale());

        ActionContext.getContext().setLocale(Locale.ITALY);
        assertEquals(Locale.ITALY, as.getLocale());

        ActionContext.setContext(new ActionContext(new HashMap<String, Object>()));
        assertEquals(defLocale, as.getLocale()); 
    }

    public void testMyActionSupport() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        assertEquals("santa", mas.doDefault());
        assertNotNull(mas.getTexts());

        assertEquals(false, mas.hasActionMessages());
        mas.validate();
        assertEquals(true, mas.hasActionMessages());
    }

    public void testSimpleGetTexts() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        checkGetTexts(mas);
    }

    public void testSimpleGetTextsWithInjectedTextProvider() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        TextProvider textProvider = container.getInstance(TextProvider.class, "system");

        assertNotNull(textProvider);

        container.inject(mas);

        checkGetTexts(mas);
    }

    private void checkGetTexts(MyActionSupport mas) {
        assertEquals("Hello World", mas.getText("hello"));
        assertEquals("not.in.bundle", mas.getText("not.in.bundle"));

        assertEquals("Hello World", mas.getText("hello", "this is default"));
        assertEquals("this is default", mas.getText("not.in.bundle", "this is default"));

        List nullList = null;
        assertEquals("Hello World", mas.getText("hello", nullList));

        String[] nullStrings = null;
        assertEquals("Hello World", mas.getText("hello", nullStrings));
    }

    public void testGetTextsWithArgs() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        assertEquals("Hello World", mas.getText("hello", "this is default", "from me")); 
        assertEquals("Hello World from me", mas.getText("hello.0", "this is default", "from me"));
        assertEquals("this is default", mas.getText("not.in.bundle", "this is default", "from me"));
        assertEquals("this is default from me", mas.getText("not.in.bundle", "this is default {0}", "from me"));

        assertEquals("not.in.bundle", mas.getText("not.in.bundle"));
    }

    public void testGetTextsWithListArgs() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        List<Object> args = new ArrayList<>();
        args.add("Santa");
        args.add("loud");
        assertEquals("Hello World", mas.getText("hello", "this is default", args)); 
        assertEquals("Hello World Santa", mas.getText("hello.0", "this is default", args)); 
        assertEquals("Hello World. This is Santa speaking loud", mas.getText("hello.1", "this is default", args));

        assertEquals("this is default", mas.getText("not.in.bundle", "this is default", args));
        assertEquals("this is default Santa", mas.getText("not.in.bundle", "this is default {0}", args));
        assertEquals("this is default Santa speaking loud", mas.getText("not.in.bundle", "this is default {0} speaking {1}", args));

        assertEquals("Hello World", mas.getText("hello", args)); 
        assertEquals("Hello World Santa", mas.getText("hello.0", args)); 
        assertEquals("Hello World. This is Santa speaking loud", mas.getText("hello.1", args));

        assertEquals("not.in.bundle", mas.getText("not.in.bundle", args));

        assertEquals("Hello World", mas.getText("hello", "this is default", (List) null));
        assertEquals("this is default", mas.getText("not.in.bundle", "this is default", (List) null));
    }

    public void testGetTextsWithArrayArgs() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        String[] args = {"Santa", "loud"};
        assertEquals("Hello World", mas.getText("hello", "this is default", args)); 
        assertEquals("Hello World Santa", mas.getText("hello.0", "this is default", args)); 
        assertEquals("Hello World. This is Santa speaking loud", mas.getText("hello.1", "this is default", args));

        assertEquals("this is default", mas.getText("not.in.bundle", "this is default", args));
        assertEquals("this is default Santa", mas.getText("not.in.bundle", "this is default {0}", args));
        assertEquals("this is default Santa speaking loud", mas.getText("not.in.bundle", "this is default {0} speaking {1}", args));

        assertEquals("Hello World", mas.getText("hello", args)); 
        assertEquals("Hello World Santa", mas.getText("hello.0", args)); 
        assertEquals("Hello World. This is Santa speaking loud", mas.getText("hello.1", args));

        assertEquals("not.in.bundle", mas.getText("not.in.bundle", args));

        assertEquals("Hello World", mas.getText("hello", "this is default", (String[]) null));
        assertEquals("this is default", mas.getText("not.in.bundle", "this is default", (String[]) null));
    }

    public void testGetTextsWithListAndStack() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        ValueStack stack = ActionContext.getContext().getValueStack();

        List<Object> args = new ArrayList<>();
        args.add("Santa");
        args.add("loud");
        assertEquals("Hello World", mas.getText("hello", "this is default", args, stack)); 
        assertEquals("Hello World Santa", mas.getText("hello.0", "this is default", args, stack)); 
        assertEquals("Hello World. This is Santa speaking loud", mas.getText("hello.1", "this is default", args, stack));

        assertEquals("this is default", mas.getText("not.in.bundle", "this is default", args, stack));
        assertEquals("this is default Santa", mas.getText("not.in.bundle", "this is default {0}", args, stack));
        assertEquals("this is default Santa speaking loud", mas.getText("not.in.bundle", "this is default {0} speaking {1}", args, stack));
    }

    public void testGetTextsWithArrayAndStack() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        ValueStack stack = ActionContext.getContext().getValueStack();

        String[] args = {"Santa", "loud"};
        assertEquals("Hello World", mas.getText("hello", "this is default", args, stack)); 
        assertEquals("Hello World Santa", mas.getText("hello.0", "this is default", args, stack)); 
        assertEquals("Hello World. This is Santa speaking loud", mas.getText("hello.1", "this is default", args, stack));

        assertEquals("this is default", mas.getText("not.in.bundle", "this is default", args, stack));
        assertEquals("this is default Santa", mas.getText("not.in.bundle", "this is default {0}", args, stack));
        assertEquals("this is default Santa speaking loud", mas.getText("not.in.bundle", "this is default {0} speaking {1}", args, stack));
    }

    public void testGetBundle() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        ResourceBundle rb = ResourceBundle.getBundle(MyActionSupport.class.getName(), new Locale("da"));
        assertEquals(rb, mas.getTexts(MyActionSupport.class.getName()));
    }

    public void testFormattingSupport() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();
        ActionContext.getContext().getValueStack().push(mas);

        mas.setVal(234d);

        String formatted = mas.getFormatted("format.number", "val");

        assertEquals("234,0", formatted);
    }

    public void testFormattingSupportWithConversionError() throws Exception {
        ActionContext.getContext().getConversionErrors().put("val", new String[]{"4567def"});
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();
        ActionContext.getContext().getValueStack().push(mas);

        mas.setVal(234d);

        String formatted = mas.getFormatted("format.number", "val");

        assertEquals("4567def", formatted);
    }

    private class MyActionSupport extends ActionSupport {

        private Double val;

        @Override
        public String doDefault() throws Exception {
            return "santa";
        }

        @Override
        public void validate() {
            super.validate(); 
            addActionMessage("validation was called");
        }

        public Double getVal() {
            return val;
        }

        public void setVal(Double val) {
            this.val = val;
        }
    }

}
