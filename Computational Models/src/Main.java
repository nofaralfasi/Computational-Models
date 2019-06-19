
import ac.il.afeka.Submission.Submission;
import ac.il.afeka.fsm.NDFSM;
import java.util.Arrays;
import java.util.List;

public class Main implements Submission, Assignment3 {
	public List<String> submittingStudentIds() {
		return Arrays.asList("123456789");
	}

	public String convert(String aNDFSMencoding) throws Exception {
		NDFSM ndfsm = new NDFSM(aNDFSMencoding);
		return ndfsm.toDFSM().encode();
	}
}
