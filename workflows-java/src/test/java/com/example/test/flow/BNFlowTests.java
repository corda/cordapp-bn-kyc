package com.example.test.flow;

import com.example.flow.ExampleFlow;
import com.google.common.collect.ImmutableList;

import net.corda.bn.flows.MembershipManagementFlowTest;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetworkParameters;
import net.corda.testing.node.StartedMockNode;
import net.corda.testing.node.TestCordapp;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;



import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;

public class BNFlowTests extends MembershipManagementFlowTest {

    private final static Logger LOGGER = Logger.getLogger(BNFlowTests.class.getName());

    private MockNetwork network;
    private StartedMockNode a;
    private StartedMockNode b;

    public BNFlowTests(int numberOfAuthorisedMembers, int numberOfRegularMembers) {
        super(numberOfAuthorisedMembers, numberOfRegularMembers);
    }

    @Before
    public void setup() {
        LOGGER.setLevel(Level.INFO);
        network = new MockNetwork(new MockNetworkParameters().withCordappsForAllNodes(ImmutableList.of(
                TestCordapp.findCordapp("com.example.contract"),
                TestCordapp.findCordapp("com.example.flow"))));
        a = network.createPartyNode(null);
        b = network.createPartyNode(null);
        // For real nodes this happens automatically, but we have to manually register the flow for tests.
        for (StartedMockNode node : ImmutableList.of(a, b)) {
            node.registerInitiatedFlow(ExampleFlow.Acceptor.class);
        }
        network.runNetwork();
    }

//    @After
//    public void tearDown() {
//        network.stopNodes();
//    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void flowRejectsInvalidIOUs() throws Exception {
        // The IOUContract specifies that IOUs cannot have negative values.
        ExampleFlow.Initiator flow = new ExampleFlow.Initiator(-1, b.getInfo().getLegalIdentities().get(0));
        CordaFuture<SignedTransaction> future = a.startFlow(flow);
        network.runNetwork();

        // The IOUContract specifies that IOUs cannot have negative values.
        exception.expectCause(instanceOf(TransactionVerificationException.class));
        future.get();
    }

    @Test
    public void activateInvalidMembershipFlow() throws Exception {
//        TestStartedNode authorisedMember = authorisedMembers.get(0);
//        UniqueIdentifier invalidMembershipId = new UniqueIdentifier();
//        try {
//            runActivateMembershipFlow(authorisedMember, invalidMembershipId, null);
//        } catch (Exception e) {
//            LOGGER.warning(e.toString());
//        }
    }
}