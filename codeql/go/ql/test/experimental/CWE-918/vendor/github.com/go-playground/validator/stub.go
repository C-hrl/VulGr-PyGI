// Code generated by depstubber. DO NOT EDIT.
// This is a simple stub for github.com/go-playground/validator, strictly for use in testing.

// See the LICENSE file for information about the licensing of the original library.
// Source: github.com/go-playground/validator (exports: Validate; functions: New)

// Package validator is a stub of github.com/go-playground/validator, generated by depstubber.
package validator

import (
	context "context"
	reflect "reflect"
)

type CustomTypeFunc func(reflect.Value) interface{}

type FieldError interface {
	ActualTag() string
	Field() string
	Kind() reflect.Kind
	Namespace() string
	Param() string
	StructField() string
	StructNamespace() string
	Tag() string
	Translate(_ interface{}) string
	Type() reflect.Type
	Value() interface{}
}

type FieldLevel interface {
	ExtractType(_ reflect.Value) (reflect.Value, reflect.Kind, bool)
	Field() reflect.Value
	FieldName() string
	GetStructFieldOK() (reflect.Value, reflect.Kind, bool)
	GetStructFieldOK2() (reflect.Value, reflect.Kind, bool, bool)
	GetStructFieldOKAdvanced(_ reflect.Value, _ string) (reflect.Value, reflect.Kind, bool)
	GetStructFieldOKAdvanced2(_ reflect.Value, _ string) (reflect.Value, reflect.Kind, bool, bool)
	GetTag() string
	Param() string
	Parent() reflect.Value
	StructFieldName() string
	Top() reflect.Value
}

type FilterFunc func([]byte) bool

type Func func(FieldLevel) bool

type FuncCtx func(context.Context, FieldLevel) bool

func New() *Validate {
	return nil
}

type RegisterTranslationsFunc func(interface{}) error

type StructLevel interface {
	Current() reflect.Value
	ExtractType(_ reflect.Value) (reflect.Value, reflect.Kind, bool)
	Parent() reflect.Value
	ReportError(_ interface{}, _ string, _ string, _ string, _ string)
	ReportValidationErrors(_ string, _ string, _ ValidationErrors)
	Top() reflect.Value
	Validator() *Validate
}

type StructLevelFunc func(StructLevel)

type StructLevelFuncCtx func(context.Context, StructLevel)

type TagNameFunc func(reflect.StructField) string

type TranslationFunc func(interface{}, FieldError) string

type Validate struct{}

func (_ *Validate) RegisterAlias(_ string, _ string) {}

func (_ *Validate) RegisterCustomTypeFunc(_ CustomTypeFunc, _ ...interface{}) {}

func (_ *Validate) RegisterStructValidation(_ StructLevelFunc, _ ...interface{}) {}

func (_ *Validate) RegisterStructValidationCtx(_ StructLevelFuncCtx, _ ...interface{}) {}

func (_ *Validate) RegisterTagNameFunc(_ TagNameFunc) {}

func (_ *Validate) RegisterTranslation(_ string, _ interface{}, _ RegisterTranslationsFunc, _ TranslationFunc) error {
	return nil
}

func (_ *Validate) RegisterValidation(_ string, _ Func, _ ...bool) error {
	return nil
}

func (_ *Validate) RegisterValidationCtx(_ string, _ FuncCtx, _ ...bool) error {
	return nil
}

func (_ *Validate) SetTagName(_ string) {}

func (_ *Validate) Struct(_ interface{}) error {
	return nil
}

func (_ *Validate) StructCtx(_ context.Context, _ interface{}) error {
	return nil
}

func (_ *Validate) StructExcept(_ interface{}, _ ...string) error {
	return nil
}

func (_ *Validate) StructExceptCtx(_ context.Context, _ interface{}, _ ...string) error {
	return nil
}

func (_ *Validate) StructFiltered(_ interface{}, _ FilterFunc) error {
	return nil
}

func (_ *Validate) StructFilteredCtx(_ context.Context, _ interface{}, _ FilterFunc) error {
	return nil
}

func (_ *Validate) StructPartial(_ interface{}, _ ...string) error {
	return nil
}

func (_ *Validate) StructPartialCtx(_ context.Context, _ interface{}, _ ...string) error {
	return nil
}

func (_ *Validate) Var(_ interface{}, _ string) error {
	return nil
}

func (_ *Validate) VarCtx(_ context.Context, _ interface{}, _ string) error {
	return nil
}

func (_ *Validate) VarWithValue(_ interface{}, _ interface{}, _ string) error {
	return nil
}

func (_ *Validate) VarWithValueCtx(_ context.Context, _ interface{}, _ interface{}, _ string) error {
	return nil
}

type ValidationErrors []FieldError

func (_ ValidationErrors) Error() string {
	return ""
}

func (_ ValidationErrors) Translate(_ interface{}) ValidationErrorsTranslations {
	return nil
}

type ValidationErrorsTranslations map[string]string