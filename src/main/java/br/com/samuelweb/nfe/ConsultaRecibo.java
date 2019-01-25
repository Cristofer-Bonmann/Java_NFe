package br.com.samuelweb.nfe;

import java.rmi.RemoteException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.transport.http.HTTPConstants;

import br.com.samuelweb.nfe.dom.ConfiguracoesNfe;
import br.com.samuelweb.nfe.exception.NfeException;
import br.com.samuelweb.nfe.util.ConstantesUtil;
import br.com.samuelweb.nfe.util.ObjetoUtil;
import br.com.samuelweb.nfe.util.WebServiceUtil;
import br.com.samuelweb.nfe.util.XmlUtil;
import br.inf.portalfiscal.nfe.schema_4.consReciNFe.TConsReciNFe;
import br.inf.portalfiscal.nfe.schema_4.retConsReciNFe.TRetConsReciNFe;
import br.inf.portalfiscal.www.nfe_400.wsdl.NFeRetAutorizacao.NFeRetAutorizacao4Stub;

/**
 * Classe Responsavel Por pegar o Retorno da NFE, apos o Envio.
 *
 * @author Samuel Oliveira
 */
class ConsultaRecibo {

	/**
	 * Retorna  aconsulta pelo número do recibo.</br>
         * 
         * <p>
         * O método monta a estrutura xml de consulta através do objeto TConsReciNFe,
         * transforma esse objeto em uma String, converte essa String para um objeto
         * OMElement e passa esse objeto como argumento para o método extraElement
         * da classe NFeConsultaProtocolo4Stub.NfeDadosMsg.
         * </p>
         * 
         * <p>
         * Após, será buscado a URL de consulta correta e o método <b>nfeRetAutorizacaoLote</b>
         * efetuará a consulta. O retorno será um objeto da classe <b>TRetConsReciNFe</b>
         * </p>
	 *
	 * @param config interface com os dados necessários para comunicação com o WebService.
	 * @param recibo String que representa o número do recibo.
	 * @param tipo ConstantesUtil.NFE ou ConstantesUtil.NFCE.
	 * @return TRetConsReciNFe objeto com o retorno da consulta.
         * 
	 * @throws NfeException
	 */

	static TRetConsReciNFe reciboNfe(ConfiguracoesNfe config, String recibo, String tipo) throws NfeException {

		try {

			/**
			 * Informaçoes do Certificado Digital.
			 */

			TConsReciNFe consReciNFe = new TConsReciNFe();
			consReciNFe.setVersao(config.getVersaoNfe());
			consReciNFe.setTpAmb(config.getAmbiente());
			consReciNFe.setNRec(recibo);

			String xml = XmlUtil.objectToXml(consReciNFe);

			OMElement ome = AXIOMUtil.stringToOM(xml);
			NFeRetAutorizacao4Stub.NfeDadosMsg dadosMsg = new NFeRetAutorizacao4Stub.NfeDadosMsg();
			dadosMsg.setExtraElement(ome);

			NFeRetAutorizacao4Stub stub = new NFeRetAutorizacao4Stub(tipo.equals(ConstantesUtil.NFCE)
					? WebServiceUtil.getUrl(config, ConstantesUtil.NFCE, ConstantesUtil.SERVICOS.CONSULTA_RECIBO)
					: WebServiceUtil.getUrl(config, ConstantesUtil.NFE, ConstantesUtil.SERVICOS.CONSULTA_RECIBO));
			// Timeout
			if (!ObjetoUtil.isEmpty(config.getTimeout())) {
				stub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, config.getTimeout());
				stub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT,
						config.getTimeout());
			}
			NFeRetAutorizacao4Stub.NfeResultMsg result = stub.nfeRetAutorizacaoLote(dadosMsg);

			return XmlUtil.xmlToObject(result.getExtraElement().toString(), TRetConsReciNFe.class);

		} catch (RemoteException | XMLStreamException | JAXBException e) {
			throw new NfeException(e.getMessage());
		}

	}
}
