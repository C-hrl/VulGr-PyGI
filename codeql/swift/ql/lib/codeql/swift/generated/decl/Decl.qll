// generated by codegen/codegen.py
private import codeql.swift.generated.Synth
private import codeql.swift.generated.Raw
import codeql.swift.elements.AstNode
import codeql.swift.elements.decl.ModuleDecl

module Generated {
  class Decl extends Synth::TDecl, AstNode {
    /**
     * Gets the module of this declaration.
     *
     * This includes nodes from the "hidden" AST. It can be overridden in subclasses to change the
     * behavior of both the `Immediate` and non-`Immediate` versions.
     */
    ModuleDecl getImmediateModule() {
      result =
        Synth::convertModuleDeclFromRaw(Synth::convertDeclToRaw(this).(Raw::Decl).getModule())
    }

    /**
     * Gets the module of this declaration.
     */
    final ModuleDecl getModule() { result = getImmediateModule().resolve() }

    /**
     * Gets the `index`th member of this declaration (0-based).
     *
     * This includes nodes from the "hidden" AST. It can be overridden in subclasses to change the
     * behavior of both the `Immediate` and non-`Immediate` versions.
     */
    Decl getImmediateMember(int index) {
      result = Synth::convertDeclFromRaw(Synth::convertDeclToRaw(this).(Raw::Decl).getMember(index))
    }

    /**
     * Gets the `index`th member of this declaration (0-based).
     */
    final Decl getMember(int index) { result = getImmediateMember(index).resolve() }

    /**
     * Gets any of the members of this declaration.
     */
    final Decl getAMember() { result = getMember(_) }

    /**
     * Gets the number of members of this declaration.
     */
    final int getNumberOfMembers() { result = count(int i | exists(getMember(i))) }
  }
}