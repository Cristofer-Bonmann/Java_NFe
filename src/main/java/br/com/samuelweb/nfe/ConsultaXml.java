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
import br.inf.portalfiscal.nfe.schema_4.consSitNFe.TConsSitNFe;
import br.inf.portalfiscal.nfe.schema_4.retConsSitNFe.TRetConsSitNFe;
import br.inf.portalfiscal.www.nfe_400.wsdl.NFeConsultaProtocolo.NFeConsultaProtocolo4Stub;

/**
 * Classe responsavel por Consultar a Situaçao do XML na SEFAZ.
 *
 * @author Samuel Oliveira - samuk.exe@hotmail.com - www.samuelweb.com.br
 */

class ConsultaXml {

	/**
	 * Método responsável por consultar o status da NF-e no SEFAZ.<br>
         * 
         * <p>
         * O método monta a estrutura xml de consulta através do objeto TConsSitNFe,
         * transforma esse objeto em uma String, converte essa String para um objeto
         * OMElement e passa esse objeto como argumento para o método extraElement
         * da classe NFeConsultaProtocolo4Stub.NfeDadosMsg.
         * </p>
         * 
         * <p>
         * Após, será buscado a URL de consulta correta e o método <b>nfeConsultaNF</b>
         * efetuará a consulta. O retorno será um objeto da classe <b>TRetConsSitNFe</b>
         * </p>
         * 
         * Obs.:</br>
         * Se houver um <b>timeout<b> configurado no argumento <b>config</b>, ele será
         * considerado.
	 *
	 * @param config interface com os dados necessários para comunicação com o WebService.
	 * @param chave String que representa a chave da NF-e(44 dígitos).
         * @param tipo ConstantesUtil.NFE ou ConstantesUtil.NFCE.
	 * @return TRetConsSitNFe objeto de retorno da consulta.
         * 
	 * @throws NfeException
         * 
	 */
	static TRetConsSitNFe consultaXml(ConfiguracoesNfe config, String chave, String tipo) throws NfeException {

		try {

			TConsSitNFe consSitNFe = new TConsSitNFe();
			consSitNFe.setVersao(config.getVersaoNfe());
			consSitNFe.setTpAmb(config.getAmbiente());
			consSitNFe.setXServ("CONSULTAR");
			consSitNFe.setChNFe(chave);

			String xml = XmlUtil.objectToXml(consSitNFe);

			if (config.isLog()) {
				System.out.println("Xml Consulta: " + xml);
			}
			OMElement ome = AXIOMUtil.stringToOM(xml);

			NFeConsultaProtocolo4Stub.NfeDadosMsg dadosMsg = new NFeConsultaProtocolo4Stub.NfeDadosMsg();
			dadosMsg.setExtraElement(ome);

			NFeConsultaProtocolo4Stub stub = new NFeConsultaProtocolo4Stub(tipo.equals(ConstantesUtil.NFCE)
					? WebServiceUtil.getUrl(config, ConstantesUtil.NFCE, ConstantesUtil.SERVICOS.CONSULTA_XML)
					: WebServiceUtil.getUrl(config, ConstantesUtil.NFE, ConstantesUtil.SERVICOS.CONSULTA_XML));
			// Timeout
			if (!ObjetoUtil.isEmpty(config.getTimeout())) {
				stub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, config.getTimeout());
				stub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT,
						config.getTimeout());
			}
			NFeConsultaProtocolo4Stub.NfeResultMsg result = stub.nfeConsultaNF(dadosMsg);

			return XmlUtil.xmlToObject(result.getExtraElement().toString(), TRetConsSitNFe.class);

		} catch (RemoteException | XMLStreamException | JAXBException e) {
			throw new NfeException(e.getMessage());
		}

	}

}