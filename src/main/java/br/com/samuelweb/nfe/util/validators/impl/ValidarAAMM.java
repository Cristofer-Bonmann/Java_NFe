package br.com.samuelweb.nfe.util.validators.impl;

import br.com.samuelweb.nfe.util.validators.RetornoValidar;
import br.com.samuelweb.nfe.util.validators.ValidadorCampo;

public class ValidarAAMM implements ValidadorCampo<String, Object> {
    @Override
    public RetornoValidar validar(String valor, Object parent) {
        //todo implementar
        return new RetornoValidarImpl(true);
    }
}
