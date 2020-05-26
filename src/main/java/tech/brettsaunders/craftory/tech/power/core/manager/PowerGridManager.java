package tech.brettsaunders.craftory.tech.power.core.manager;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.BaseProvider;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;

public class PowerGridManager implements Externalizable {

  HashSet<BaseCell> cells = new HashSet<>();
  HashSet<BaseProvider> generators = new HashSet<>();
  HashSet<BaseMachine> machines = new HashSet<>();

  public void doStuff() {
    int produced = whatDidYouMakeToday();
    int needed = whatDoTheyNeed();
    if (needed > produced) {
      produced += raidTheBank(needed-produced);
    }
    if (produced > needed) {
      int extra = giveThePeopleWhatTheyWant(produced);
      fillTheBanks(extra);
    } else {
      shareThisAmongstThePeople(produced);
    }
  }

  /* Calculates how much energy the generators produced this tick */
  private int whatDidYouMakeToday() {
    int amount = 0;
    for (BaseProvider generator: generators) {
      amount += generator.howMuchCanYouGiveMe();
    }
    return amount;
  }

  /* Calculates how much energy the machines can take this tick */
  private int whatDoTheyNeed() {
    int amount = 0;
    for (BaseMachine machine: machines){
      amount += machine.howMuchDoYouNeed();
    }
    return amount;
  }

  /**
   * Attempts to gather energy from storage
   * @param goal amount of energy to gather
   * @return amount gathered
   */
  private int raidTheBank(int goal) {
    int amount = 0;
    for (BaseCell cell: cells) {
      EnergyStorage e = cell.getEnergyStorage();
      amount += e.extractEnergy((goal - amount),false);
      if(amount >= goal) break;
    }
    return amount;
  }

  /**
   * Puts excess energy into storage
   * @param amount the amount of excess energy
   */
  private void fillTheBanks(int amount) {
    for (BaseCell cell: cells) {
      amount -= cell.receiveEnergy(null, amount, false);
    }
  }

  /* Provides the machines with energy.
   * Used when there is enough for all the machines  */
  private int giveThePeopleWhatTheyWant(int amount) {
    for (BaseMachine machine: machines) {
      amount -= machine.receiveEnergy(null,amount,false);
    }
    return amount;
  }

  /* Shares the available energy amongst the machines
   * Used when there is not enough for all machines  */
  private void shareThisAmongstThePeople(int amount) {
    int allotment = amount/machines.size();
    while (amount > 0) {
      for (BaseMachine machine: machines) {
        amount -= machine.receiveEnergy(null,allotment,false);
      }
    }
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(cells);
    out.writeObject(generators);
    out.writeObject(machines);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    cells = (HashSet<BaseCell>) in.readObject();
    generators = (HashSet<BaseProvider>) in.readObject();
    machines = (HashSet<BaseMachine>) in.readObject();
  }
}
