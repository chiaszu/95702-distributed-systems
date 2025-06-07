// Chia-Szu, Kuo (chiaszuk)

import java.sql.Timestamp;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Scanner;

/**
 * This class represents a simple BlockChain.
 */
public class BlockChain {
    private ArrayList<Block> blocks;
    private String chainHash;
    private int hashesPerSecond;

    /**
     * This BlockChain has exactly three instance members - an ArrayList to hold Blocks and a chain
     * hash to hold a SHA256 hash of the most recently added Block. It also maintains an instance
     * variable holding the approximate number of hashes per second on this computer. This constructor
     * creates an empty ArrayList for Block storage. This constructor sets the chain hash to the empty
     * string and sets hashes per second to 0.
     */
    public BlockChain() {
        blocks = new ArrayList<>();
        chainHash = "";
        hashesPerSecond = 0;
    }

    /**
     * A new Block is being added to the BlockChain. This new block's previous hash must hold the hash
     * of the most recently added block. After this call on addBlock, the new block becomes the most
     * recently added block on the BlockChain. The SHA256 hash of every block must exhibit proof of
     * work, i.e., have the requisite number of leftmost 0's defined by its difficulty.
     *
     * @param newBlock - is added to the BlockChain as the most recent block
     */
    public void addBlock(Block newBlock) {
        // Set previous hash to the chain hash
        newBlock.setPreviousHash(chainHash);

        // Perform proof of work
        chainHash = newBlock.proofOfWork();

        // Add block to chain
        blocks.add(newBlock);
    }

    /**
     * This method computes exactly 2 million hashes and times how long that process takes. So, hashes
     * per second is approximated as (2 million / number of seconds). It is run on start up and sets the
     * instance variable hashesPerSecond. It uses a simple string - "00000000" to hash.
     */
    public void computeHashesPerSecond() {
        String baseString = "00000000";
        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("SHA-256");

            long startTime = System.currentTimeMillis();

            // Compute 2 million hashes
            for (int i = 0; i < 2000000; i++) {
                String data = baseString + i;
                digest.digest(data.getBytes(StandardCharsets.UTF_8));
            }

            long endTime = System.currentTimeMillis();
            double elapsedTimeInSeconds = (endTime - startTime) / 1000.0;

            // Calculate hashes per second
            hashesPerSecond = (int) (2000000 / elapsedTimeInSeconds);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error in computing hashes per second: " + e.getMessage());
        }
    }

    /**
     * If the chain only contains one block, the genesis block at position 0, this routine computes the
     * hash of the block and checks that the hash has the requisite number of leftmost 0's (proof of work)
     * as specified in the difficulty field. It also checks that the chain hash is equal to this computed hash.
     * If either check fails, return an error message. Otherwise, return the string "TRUE". If the chain has
     * more blocks than one, begin checking from block one. Continue checking until you have validated
     * the entire chain. The first check will involve a computation of a hash in Block 0 and a comparison
     * with the hash pointer in Block 1. If they match and if the proof of work is correct, go and visit the
     * next block in the chain. At the end, check that the chain hash is also correct.
     *
     * @return "TRUE" if the chain is valid, otherwise return a string with an appropriate error message
     */
    public String isChainValid() {
        // If chain is empty
        if (blocks.isEmpty()) {
            return "Chain is empty";
        }

        // If only genesis block exists
        if (blocks.size() == 1) {
            Block genesisBlock = blocks.get(0);
            String genesisHash = genesisBlock.calculateHash();

            // Check proof of work
            String target = new String(new char[genesisBlock.getDifficulty()]).replace('\0', '0');
            if (!genesisHash.substring(0, genesisBlock.getDifficulty()).equals(target)) {
                return "Improper hash on node 0 Does not begin with " + target;
            }

            // Check chain hash
            if (!chainHash.equals(genesisHash)) {
                return "Chain hash not equal to hash of genesis block";
            }

            return "TRUE";
        }

        // Check each block in the chain
        for (int i = 0; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            String blockHash = currentBlock.calculateHash();

            // Check proof of work
            String target = new String(new char[currentBlock.getDifficulty()]).replace('\0', '0');
            if (!blockHash.substring(0, currentBlock.getDifficulty()).equals(target)) {
                return "Improper hash on node " + i + " Does not begin with " + target;
            }

            // Check hash pointer consistency (except for genesis block)
            if (i > 0) {
                Block previousBlock = blocks.get(i - 1);
                String previousHash = previousBlock.calculateHash();

                if (!currentBlock.getPreviousHash().equals(previousHash)) {
                    return "Improper previous hash on node " + i;
                }
            }
        }

        // Verify chain hash
        if (!chainHash.equals(blocks.get(blocks.size() - 1).calculateHash())) {
            return "Chain hash not equal to hash of most recent block";
        }

        return "TRUE";
    }

    /**
     * This routine repairs the chain. It checks the hashes of each block and ensures that any illegal
     * hashes are recomputed. After this routine is run, the chain will be valid. The routine does not
     * modify any difficulty values. It computes new proof of work based on the difficulty specified in the
     * Block.
     */
    public void repairChain() {
        if (blocks.isEmpty()) {
            return;
        }

        // If only genesis block
        if (blocks.size() == 1) {
            Block genesisBlock = blocks.get(0);
            chainHash = genesisBlock.proofOfWork();
            return;
        }

        // Repair each block starting from genesis
        for (int i = 0; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);

            // For blocks after genesis, set the correct previous hash
            if (i > 0) {
                Block previousBlock = blocks.get(i - 1);
                String previousHash = previousBlock.calculateHash();
                currentBlock.setPreviousHash(previousHash);
            }

            currentBlock.proofOfWork();
        }

        // Update chain hash
        chainHash = blocks.get(blocks.size() - 1).calculateHash();
    }

    /**
     * @return the chain hash
     */
    public String getChainHash() {
        return chainHash;
    }

    /**
     * @return current timestamp
     */
    public Timestamp getTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * @return the latest block in the chain
     */
    public Block getLatestBlock() {
        if (blocks.isEmpty()) {
            return null;
        }
        return blocks.get(blocks.size() - 1);
    }

    /**
     * Get the number of blocks in the chain
     *
     * @return the chain size
     */
    public int getChainSize() {
        return blocks.size();
    }

    /**
     * @return the hashes per second
     */
    public int getHashesPerSecond() {
        return hashesPerSecond;
    }

    /**
     * @param i - position in the chain
     * @return the Block at position i
     */
    public Block getBlock(int i) {
        if (i >= 0 && i < blocks.size()) {
            return blocks.get(i);
        }
        return null;
    }

    /**
     * Compute and return the total difficulty of all blocks on the chain.
     *
     * @return the sum of all block difficulties
     */
    public int getTotalDifficulty() {
        int totalDifficulty = 0;
        for (Block block : blocks) {
            totalDifficulty += block.getDifficulty();
        }
        return totalDifficulty;
    }

    /**
     * Compute and return the expected number of hashes required for the entire chain.
     *
     * @return the expected number of hashes needed for the chain
     */
    public double getTotalExpectedHashes() {
        int totalDifficulty = getTotalDifficulty();
        // 16^difficulty gives us the expected number of hashes
        return Math.pow(16, totalDifficulty);
    }


    /**
     * This method uses the toString method defined on each individual block.
     *
     * @return a String representation of the entire chain is returned.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"ds_chain\" : [ ");

        for (int i = 0; i < blocks.size(); i++) {
            sb.append(blocks.get(i).toString());
            if (i < blocks.size() - 1) {
                sb.append(",\n");
            }
        }

        sb.append("\n ], \"chainHash\":\"").append(chainHash).append("\"}");
        return sb.toString();
    }

    /**
     * This routine acts as a test driver for your BlockChain.
     *
     * On start up, this routine creates a BlockChain object and adds the Genesis block.
     * The Genesis block is created with an empty string as the previous hash and a difficulty of 2.
     *
     * All blocks added to the BlockChain will have a difficulty passed to the program by the user.
     * All hashes will have the proper number of zero hex digits representing the most significant
     * nibbles in the hash. A nibble is 4 bits. If the difficulty is specified as three, then all
     * hashes will begin with 3 or more zero hex digits (or 3 nibbles, or 12 zero bits).
     */
    public static void main(String[] args) {
        // Create the blockchain and add genesis block with difficulty 2
        BlockChain blockchain = new BlockChain();
        blockchain.computeHashesPerSecond();

        // Create genesis block
        Block genesisBlock = new Block(0, blockchain.getTime(), "Genesis", 2);
        blockchain.addBlock(genesisBlock);

        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        while (choice != 6) {
            displayMenu();

            try {
                choice = Integer.parseInt(scanner.nextLine());
                processChoice(blockchain, choice, scanner);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("0. View basic blockchain status.");
        System.out.println("1. Add a transaction to the blockchain.");
        System.out.println("2. Verify the blockchain.");
        System.out.println("3. View the blockchain.");
        System.out.println("4. Corrupt the chain.");
        System.out.println("5. Hide the corruption by recomputing hashes.");
        System.out.println("6. Exit");
    }

    private static void processChoice(BlockChain blockchain, int choice, Scanner scanner) {
        long startTime, endTime, executionTime;

        switch (choice) {
            case 0:
                displayBlockchainStatus(blockchain);
                break;

            case 1:
                System.out.println("Enter difficulty > 1");
                int difficulty = Integer.parseInt(scanner.nextLine());

                System.out.println("Enter transaction");
                String transaction = scanner.nextLine();

                startTime = System.currentTimeMillis();
                Block newBlock = new Block(blockchain.getChainSize(), blockchain.getTime(), transaction, difficulty);
                blockchain.addBlock(newBlock);
                endTime = System.currentTimeMillis();
                executionTime = endTime - startTime;

                System.out.println("Total execution time to add this block was " + executionTime + " milliseconds");
                break;

            case 2:
                System.out.println("Verifying entire chain");
                startTime = System.currentTimeMillis();
                String isValid = blockchain.isChainValid();
                endTime = System.currentTimeMillis();
                executionTime = endTime - startTime;

                System.out.println("Chain verification: " + isValid);
                System.out.println("Total execution time required to verify the chain was " + executionTime + " milliseconds");
                break;

            case 3:
                System.out.println("View the Blockchain");
                System.out.println(blockchain.toString());
                break;

            case 4:
                System.out.println("Corrupt the Blockchain");
                System.out.println("Enter block ID of block to corrupt");
                int blockId = Integer.parseInt(scanner.nextLine());

                System.out.println("Enter new data for block " + blockId);
                String newData = scanner.nextLine();

                if (blockId >= 0 && blockId < blockchain.getChainSize()) {
                    blockchain.getBlock(blockId).setData(newData);
                    System.out.println("Block " + blockId + " now holds " + newData);
                } else {
                    System.out.println("Invalid block ID");
                }
                break;

            case 5:
                System.out.println("Repairing the entire chain");
                startTime = System.currentTimeMillis();
                blockchain.repairChain();
                endTime = System.currentTimeMillis();
                executionTime = endTime - startTime;

                System.out.println("Total execution time required to repair the chain was " + executionTime + " milliseconds");
                break;

            case 6:
                // Exit
                break;

            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void displayBlockchainStatus(BlockChain blockchain) {
        System.out.println("Current size of chain: " + blockchain.getChainSize());
        System.out.println("Difficulty of most recent block: " + blockchain.getLatestBlock().getDifficulty());
        System.out.println("Total difficulty for all blocks: " + blockchain.getTotalDifficulty());
        System.out.println("Experimented with 2,000,000 hashes.");
        System.out.println("Approximate hashes per second on this machine: " + blockchain.getHashesPerSecond());
        System.out.println("Expected total hashes required for the whole chain: " + blockchain.getTotalExpectedHashes());
        System.out.println("Nonce for most recent block: " + blockchain.getLatestBlock().getNonce());
        System.out.println("Chain hash: " + blockchain.getChainHash());
    }
}