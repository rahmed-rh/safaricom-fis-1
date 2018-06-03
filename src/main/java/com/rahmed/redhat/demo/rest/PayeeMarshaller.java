package com.rahmed.redhat.demo.rest;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.infinispan.protostream.MessageMarshaller;
import org.infinispan.protostream.UnknownFieldSet;
import org.infinispan.protostream.UnknownFieldSetHandler;

public class PayeeMarshaller implements MessageMarshaller<Payee>, UnknownFieldSetHandler<Payee> {

	@Override
	public String getTypeName() {
		return "uaex.Payee";
	}

	@Override
	public Class<? extends Payee> getJavaClass() {
		return Payee.class;
	}

	@Override
	public Payee readFrom(ProtoStreamReader reader) throws IOException {
		int id = reader.readInt("id");
		

		// Read them out of order. It still works but logs a warning!
		String name = reader.readString("name");
		String bankName = reader.readString("bankName");
		String accountNumber = reader.readString("accountNumber");

		
		Payee payee = new Payee();
		payee.setId(id);
		payee.setName(name);
		payee.setBankName(bankName);
		payee.setAccountNumber(accountNumber);
		
		return payee;
	}

	@Override
	public void writeTo(ProtoStreamWriter writer, Payee payee) throws IOException {
		writer.writeInt("id", payee.getId().intValue());
		writer.writeString("name", payee.getName());
		writer.writeString("bankName", payee.getBankName());
		writer.writeString("accountNumber", payee.getAccountNumber());
	}

	@Override
	public UnknownFieldSet getUnknownFieldSet(Payee payee) {
		return payee.getUnknownFieldSet();
	}

	@Override
	public void setUnknownFieldSet(Payee payee, UnknownFieldSet unknownFieldSet) {
		payee.setUnknownFieldSet(unknownFieldSet);
		
	}

	
}