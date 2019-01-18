package br.com.samuelweb.nfe;

import java.rmi.RemoteException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.transport.http.HTTPConstants;

import br.com.samuelweb.nfe.dom.ConfiguracoesNfe;
import br.com.samuelweb.nfe.exception.NfeException;
import br.com.samuelweb.nfe.util.Estados;
import br.com.samuelweb.nfe.util.ObjetoUtil;
import br.com.samuelweb.nfe.util.WebServiceUtil;
import br.com.samuelweb.nfe.util.XmlUtil;
import br.inf.portalfiscal.nfe.schema.consCad.TConsCad;
import br.inf.portalfiscal.nfe.schema.consCad.TUfCons;
import br.inf.portalfiscal.nfe.schema.retConsCad.TRetConsCad;
import br.inf.portalfiscal.www.nfe_400.wsdl.CadConsultaCadastro.CadConsultaCadastro4Stub;
import br.inf.portalfiscal.www.nfe_400.wsdl.CadConsultaCadastro.rs.CadConsultaCadastro4StubRs;

/**
 * Resposável por consultar o cadastro de contribuinte ICMS
 *
 * @author Samuel Oliveira - samuk.exe@hotmail.com - www.samuelweb.com.br
 * 
 */

class ConsultaCadastro {

	static final String CNPJ = "CNPJ";
	static final String CPF = "CPF";

	/**
	 * Efetua a consulta do cadastro de contribuinte ICMS.<br>
         * 
         * Este método cria a estrutura do XML de consulta do cadastro, busca a URL de
         * consulta chamando o método getUrlConsultaCadastro na classe WebServiceUtil
         * e efetua a consulta chamando <b>stub.consultaCadastro(dadosMsg).</b> Seu
         * resultado é então retornado em um objeto <b>TRetConsCad</b>.<br>
         * 
         * <p>
         * Para consulta de contribuintes em RS são usados classes específicas.<br>
         * Se houver um <b>timeout<b> configurado no parâmetro <b>config</b>, ele será
         * considera do fazer a consulta.
	 * </p>
         * 
         * Ex.:<br>
         * {@code 
         * TRetConsCad retorno = Nfe.consultaCadastro(ConstantesUtil.TIPOS.CNPJ, "XXXXXXXXXXXXXX", Estados.SP);
         * if (retorno.getInfCons().getCStat().equals(StatusEnum.CADASTRO_ENCONTRADO.getCodigo())) {
         *     retorno.getInfCons().getInfCad().get(0).getXNome());
         *     retorno.getInfCons().getInfCad().get(0).getCNPJ());
         *     retorno.getInfCons().getInfCad().get(0).getIE());
         * }
         * }
         * 
	 * @param config interface com os dados necessários para comunicação com o WebService.
	 * @param tipo CPF ou CNPJ.
         * @param cnpjCpf String com o CPF ou CNPJ.
         * @param estado objeto Estado com o UF da consulta do cadastro.
	 * @return TRetConsCad objeto com o retorno da consulta.
	 * @throws NfeException
         * 
         * @see WebServiceUtil
         * @see Estados
	 */

	static TRetConsCad consultaCadastro(ConfiguracoesNfe config, String tipo, String cnpjCpf, Estados estado)
			throws NfeException {

		try {

			TConsCad consCad = new TConsCad();
			consCad.setVersao("2.00");

			TConsCad.InfCons infCons = new TConsCad.InfCons();
			if (CNPJ.equals(tipo)) {
				infCons.setCNPJ(cnpjCpf);
			} else {
				infCons.setCPF(cnpjCpf);
			}
			infCons.setXServ("CONS-CAD");
			infCons.setUF(TUfCons.valueOf(estado.toString()));

			consCad.setInfCons(infCons);

			String xml = XmlUtil.objectToXml(consCad);

			if (config.isLog()) {
				System.out.println("Xml Consulta: " + xml);
			}
			OMElement ome = AXIOMUtil.stringToOM(xml);

			if (estado.equals(Estados.RS)) {
				CadConsultaCadastro4StubRs.NfeDadosMsg_type0 dadosMsgRS = new CadConsultaCadastro4StubRs.NfeDadosMsg_type0();
				dadosMsgRS.setExtraElement(ome);

				CadConsultaCadastro4StubRs stubRS = new CadConsultaCadastro4StubRs(
						WebServiceUtil.getUrlConsultaCadastro(config, estado.toString()));

				// Timeout
				if (!ObjetoUtil.isEmpty(config.getTimeout())) {
					stubRS._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, config.getTimeout());
					stubRS._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT,
							config.getTimeout());
				}

				CadConsultaCadastro4StubRs.ConsultaCadastro consultaCadastro = new CadConsultaCadastro4StubRs.ConsultaCadastro();
				consultaCadastro.setNfeDadosMsg(dadosMsgRS);

				CadConsultaCadastro4StubRs.NfeResultMsg resultRS = stubRS.consultaCadastro(consultaCadastro);

				return XmlUtil.xmlToObject(resultRS.getConsultaCadastroResult().getExtraElement().toString(),
						TRetConsCad.class);

			} else {
				CadConsultaCadastro4Stub.NfeDadosMsg dadosMsg = new CadConsultaCadastro4Stub.NfeDadosMsg();
				dadosMsg.setExtraElement(ome);

				CadConsultaCadastro4Stub stub = new CadConsultaCadastro4Stub(
						WebServiceUtil.getUrlConsultaCadastro(config, estado.toString()));

				// Timeout
				if (!ObjetoUtil.isEmpty(config.getTimeout())) {
					stub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, config.getTimeout());
					stub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT,
							config.getTimeout());
				}

				CadConsultaCadastro4Stub.NfeResultMsg result = stub.consultaCadastro(dadosMsg);

				return XmlUtil.xmlToObject(result.getExtraElement().toString(), TRetConsCad.class);
			}

		} catch (RemoteException | XMLStreamException | JAXBException e) {
			throw new NfeException(e.getMessage());
		}

	}

}