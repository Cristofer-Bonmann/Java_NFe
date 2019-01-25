/**
 * 
 */
package br.com.samuelweb.nfe;

import br.com.samuelweb.nfe.dom.ConfiguracoesIniciaisNfe;
import br.com.samuelweb.nfe.dom.Enum.TipoManifestacao;
import br.com.samuelweb.nfe.exception.NfeException;
import br.com.samuelweb.nfe.util.CertificadoUtil;
import br.com.samuelweb.nfe.util.Estados;
import br.com.samuelweb.nfe.util.XmlUtil;
import br.inf.portalfiscal.nfe.schema.envEventoCancNFe.TEnvEvento;
import br.inf.portalfiscal.nfe.schema.envEventoCancNFe.TRetEnvEvento;
import br.inf.portalfiscal.nfe.schema.retConsCad.TRetConsCad;
import br.inf.portalfiscal.nfe.schema.retdistdfeint.RetDistDFeInt;
import br.inf.portalfiscal.nfe.schema_4.enviNFe.TEnviNFe;
import br.inf.portalfiscal.nfe.schema_4.enviNFe.TRetEnviNFe;
import br.inf.portalfiscal.nfe.schema_4.inutNFe.TInutNFe;
import br.inf.portalfiscal.nfe.schema_4.inutNFe.TRetInutNFe;
import br.inf.portalfiscal.nfe.schema_4.retConsReciNFe.TRetConsReciNFe;
import br.inf.portalfiscal.nfe.schema_4.retConsSitNFe.TRetConsSitNFe;
import br.inf.portalfiscal.nfe.schema_4.retConsStatServ.TRetConsStatServ;

import javax.xml.bind.JAXBException;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com - www.samuelweb.com.br
 */
public class Nfe {

	/**
	 * Construtor privado
	 */
	private Nfe() {
	}

	/**
	 * Classe Reponsavel Por Consultar a Distribuiçao da NFE na SEFAZ
	 *
	 * @param tipoCliente
	 * Informar DistribuicaoDFe.CPF ou DistribuicaoDFe.CNPJ
	 * @param cpfCnpj
	 * @param tipoConsulta
	 * Informar DistribuicaoDFe.NSU ou DistribuicaoDFe.CHAVE
	 * @param nsuChave
	 * @return
	 * @throws NfeException
	 */
	public static RetDistDFeInt distribuicaoDfe(String tipoCliente, String cpfCnpj, String tipoConsulta,
			String nsuChave) throws NfeException {

		return DistribuicaoDFe.consultaNfe(CertificadoUtil.iniciaConfiguracoes(), tipoCliente, cpfCnpj, tipoConsulta,
				nsuChave);

	}

	/**
	 * Metodo Responsavel Buscar o Status de Serviço do Servidor da Sefaz
	 *
	 * @param tipo informar ConstantesUtil.NFE ou ConstantesUtil.NFCE
	 * @return TRetConsStatServ - objeto a mensagem de retorno da 
         * transmissão.
	 * @throws NfeException
	 */
	public static TRetConsStatServ statusServico(String tipo) throws NfeException {

		return Status.statusServico(CertificadoUtil.iniciaConfiguracoes(), tipo);

	}

	/**
	 * Método Reponsavel Por Consultar o status da NFE na SEFAZ
	 *
	 * @param chave String que representa a chave da NF-e(44 dígitos).
	 * @param tipo ConstantesUtil.NFE ou ConstantesUtil.NFCE.
	 * @return TRetConsSitNF objeto de retorno da consulta.
         * 
	 * @throws NfeException
	 */
	public static TRetConsSitNFe consultaXml(String chave, String tipo) throws NfeException {

		return ConsultaXml.consultaXml(CertificadoUtil.iniciaConfiguracoes(), chave, tipo);

	}

	/**
	 * Classe Reponsavel Por Consultar o cadastro do Cnpj/CPF na SEFAZ
	 *
	 * @param tipo ConstantesUtil.NFE ou ConstantesUtil.NFCE.
	 * @param cnpjCpf o número do CPF ou CNPJ sem ".","-" e "/".
	 * @param estado objeto Estado com o UF da consulta do cadastro.
	 * @return TRetConsCad objeto com o retorno da consulta.
         * 
	 * @throws NfeException
	 */
	public static TRetConsCad consultaCadastro(String tipo, String cnpjCpf, Estados estado) throws NfeException {

		return ConsultaCadastro.consultaCadastro(CertificadoUtil.iniciaConfiguracoes(), tipo, cnpjCpf, estado);

	}

	/**
	 * Método para consulta de NF-e através do número do recibo(campo NRec).
         * 
         * O númedo do recibo é retornado quando, no caso de WebServices assíncronos,  
         * o lote é processado com sucesso e a NF-e aguarda autorização.
	 *
	 * @param recibo String que representa o número do recibo.
	 * @param tipo ConstantesUtil.NFE ou ConstantesUtil.NFCE.
	 * @return TRetConsReciNFe objeto com o retorno da consulta.
         * 
	 * @throws NfeException
         * 
	 */
	public static TRetConsReciNFe consultaRecibo(String recibo, String tipo) throws NfeException {

		return ConsultaRecibo.reciboNfe(CertificadoUtil.iniciaConfiguracoes(), recibo, tipo);

	}

	/**
         * Método responsável por inutilizar a nota fiscal.<br>
         * <p>
         * Inutilizar uma faixa de númeração de nota fiscal é basicamente informar
         * ao fisco quais numerações não serão utilizadas por razão de 
         * quebra de sequência. 
         * </p>
         * 
         * <p>
         * A inutilização só é possível caso a numeração ainda não tenha
         * sido utilizada em nenhuma NFe (seja ela autorizada, cancelada ou denegada).
         * </p>
	 *
	 * @param id usado para identificar a faixa de numeração a ser inutilizada.
         * composto por: Código da UF + Ano (2 posições) + CNPJ + modelo + série
         * + número inicial e número final precedida do literal "ID".
	 * @param motivo String com a descrição do motivo da inutilização.
	 * @param tipo ConstantesUtil.NFE ou ConstantesUtil.NFCE.
         * @param validar boolean para indicar se a estrutura do XML deve ser 
         * ou não valida antes de ser enviado para o WebService.
         * 
	 * @return TRetInutNFe objeto de retorno da inutilização.
         * 
	 * @throws NfeException
	 */
	public static TRetInutNFe inutilizacao(String id, String motivo, String tipo, boolean validar) throws NfeException {

		return Inutilizar.inutiliza(CertificadoUtil.iniciaConfiguracoes(), id, motivo, tipo, validar);

	}

	/**
	 * Classe Reponsavel Por criar o Objeto de Inutilização No tipo Informar
	 * ConstantesUtil.NFE ou ConstantesUtil.NFCE Id = Código da UF + Ano (2
	 * posições) + CNPJ + modelo + série + número inicial e número final precedida
	 * do literal “ID”
	 *
	 * @param id
	 * @param valida
	 * @param tipo
	 * @return
	 * @throws NfeException
	 */
	public static TInutNFe criaObjetoInutilizacao(String id, String motivo, String tipo)
			throws NfeException, JAXBException {

		TInutNFe inutNFe = Inutilizar.criaObjetoInutiliza(CertificadoUtil.iniciaConfiguracoes(), id, motivo, tipo);

		String xml = XmlUtil.objectToXml(inutNFe);
		xml = xml.replaceAll(" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");

		return XmlUtil.xmlToObject(Assinar.assinaNfe(CertificadoUtil.iniciaConfiguracoes(), xml, Assinar.INFINUT),
				TInutNFe.class);

	}

    /**
     * Metodo para Montar a NFE.
     *
     * @param enviNFe
     * @param valida
     * @return
     * @throws NfeException
     */
    public static TEnviNFe montaNfe(TEnviNFe enviNFe, boolean valida) throws NfeException {

        return Enviar.montaNfe(CertificadoUtil.iniciaConfiguracoes(), enviNFe, valida);

    }

	/**
	 * Metodo para Enviar a NFE. No tipo Informar ConstantesUtil.NFE ou
	 * ConstantesUtil.NFCE
	 *
	 * @param enviNFe
	 * @param tipo
	 * @return
	 * @throws NfeException
	 */
	public static TRetEnviNFe enviarNfe(TEnviNFe enviNFe, String tipo) throws NfeException {

		return Enviar.enviaNfe(CertificadoUtil.iniciaConfiguracoes(), enviNFe, tipo);

	}

	/**
	 * Método responsável pelo cancelamento de Nf-e.
	 * ConstantesUtil.NFCE
	 *
	 * @param envEvento
	 * @return
	 * @throws NfeException
	 */
	public static TRetEnvEvento cancelarNfe(TEnvEvento envEvento, boolean valida, String tipo) throws NfeException {

		return Cancelar.eventoCancelamento(CertificadoUtil.iniciaConfiguracoes(), envEvento, valida, tipo);

	}

    /**
     * * Assina o Cancenlamento
     *
     * @param envEvento
     * @return
     * @throws NfeException
     */
    public static String assinaCancelamento(ConfiguracoesIniciaisNfe config, String xml) throws NfeException {
        return Assinar.assinaNfe(config, xml, Assinar.EVENTO);
    }

    /**
     * * Metodo para Enviar o EPEC.
     * No tipo Informar ConstantesUtil.NFE ou ConstantesUtil.NFCE
     *
     * @param envEvento
     * @return
     * @throws NfeException
     */
    public static br.inf.portalfiscal.nfe.schema.envEpec.TRetEnvEvento enviarEpec(br.inf.portalfiscal.nfe.schema.envEpec.TEnvEvento envEvento, boolean valida, String tipo) throws NfeException {

        return Epec.eventoEpec(CertificadoUtil.iniciaConfiguracoes(),envEvento, valida, tipo);

    }

	/**
	 * * Metodo para Envio da Carta De Correção da NFE. No tipo Informar
	 * ConstantesUtil.NFE ou ConstantesUtil.NFCE
	 *
	 * @param evento
	 * @param valida
	 * @param tipo
	 * @return
	 * @throws NfeException
	 */
	public static br.inf.portalfiscal.nfe.schema.envcce.TRetEnvEvento cce(
			br.inf.portalfiscal.nfe.schema.envcce.TEnvEvento evento, boolean valida, String tipo) throws NfeException {

		return CartaCorrecao.eventoCCe(CertificadoUtil.iniciaConfiguracoes(), evento, valida, tipo);

	}

	/**
	 * Metodo para Manifestação da NFE.
	 *
	 * @param envEvento
	 * @param valida
	 * @return
	 * @throws NfeException
	 */
	public static br.inf.portalfiscal.nfe.schema.envConfRecebto.TRetEnvEvento manifestacao(String chave,
			TipoManifestacao manifestacao, String cnpj, String motivo, String data) throws NfeException {

		return ManifestacaoDestinatario.eventoManifestacao(CertificadoUtil.iniciaConfiguracoes(), chave, manifestacao,
				cnpj, data, motivo);

	}

}
